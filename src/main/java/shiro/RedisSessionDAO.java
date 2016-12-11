package shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Redis 是一个高速的分布式缓存。
 虽然配置 EhCache 提升了效率，但是，Session 仍然存储在 Server 的内存里（Shiro 默认使用 MemorySessionDAO 把 Session 存储在 ConcurrentMap 里），当有大量的用户登录后 Server 的内存就会急剧增加，而且由于 Server 之间内存里的 Session 不能共享，所以没法实现集群。为了解决这两个问题，我们本地仍然使用 EhCache 缓存 Session，但是 Session 存储在 Redis 里。
 流程说明：
 Servlet 容器在用户浏览器首次访问后会产生 Session，并将 Session 的 ID 保存到 Cookie 中(浏览器不同 ID 不一定相同)，同时 Shiro 会将该 Session 缓存到 Redis 中
 用户登录认证成功后 Shiro 会修改 Session 属性，添加用户认证成功标识，并同步修改 Redis 中 Session
 用户发起请求后，Shiro 会先判断本地 EhCache 缓存中是否存在该 Session，如果有，直接从本地EhCache 缓存中读取，如果没有再从 Redis 中读取 Session，并在此时判断 Session 是否认证通过，如果认证通过将该 Session 缓存到本地 EhCache 中
 如果 Session 发生改变，或被删除（用户退出登录），先对 Redis 中 Session 做相应修改（修改或删除）；再通过 Redis 消息通道发布缓存失效消息，通知其它节点 EhCache 失效
 注意
 本地缓存的过期时间要小于 Redis 上 Session 的过期时间，防止本地缓存里 Session 过期了 Redis 上 Session 还没有过期
 Session 不要存不必要的东西
 * Created by chengseas on 2016/12/11.
 */
public class RedisSessionDAO extends CachingSessionDAO{
    private static Logger logger = LoggerFactory.getLogger(RedisSessionDAO.class);

    // 登录成功的信息存储在 session 的这个 attribute 里.
    private static final String AUTHENTICATED_SESSION_KEY =
            "org.apache.shiro.subject.support.DefaultSubjectContext_AUTHENTICATED_SESSION_KEY";

    private String keyPrefix = "shiro_redis_session:";
    private String deleteChannel = "shiro_redis_session:delete";
    private int timeToLiveSeconds = 1800; //Expiration of Jedis's key, unit:second
    private RedisManager redisManager;

    /**
     * DefaultSessionManager 创建完 session 后会调用该方法。
     * 把 session 保持到 Redis。
     * 返回 Session ID；主要此处返回的 ID.equals(session.getId())
     */
    @Override
    protected Serializable doCreate(Session session){
        logger.debug("=> Create session with ID [{}]", session.getId());

        //创建一个 Id 并设置给 Session
        Serializable sessionId = this.generateSessionId(session);
        assignSessionId(session, sessionId);

        // session 由 Redis 缓存失效决定
        String key = SerializationUtils.sessionKey(keyPrefix, session);
        String value = SerializationUtils.sessionFromString(session);
        redisManager.setex(key, value, timeToLiveSeconds);

        return  sessionId;
    }

    /**
     * 决定从本地 Cache 还是从 Redis 读取 Session
     * @param sessionId
     * @return
     * @throws UnknownSessionException
     */
    public Session readSession(Serializable sessionId) throws UnknownSessionException{
        Session session = getCachedSession(sessionId);

        // 1. 如果本地缓存没有，则从 Redis中读取
        // 2. ServerA 登录了， ServerB 没有登录但缓存里由此 session， 所以从 Redis读取而不是直接用缓存里的
        if ( session == null ||(
                session.getAttribute(AUTHENTICATED_SESSION_KEY) != null
                        && !(Boolean)session.getAttribute(AUTHENTICATED_SESSION_KEY)
                )){
            session = doReadSession(sessionId);
            if (session == null){
                throw  new UnknownSessionException("There is no session with id [" + sessionId + "]");
            }
            return session;
        }
        return session;
    }

    /**
     * 从 Redis 上读取 session， 并缓存到本地 Cache
     * @param sessionId
     * @return
     */
    @Override
    protected Session doReadSession(Serializable sessionId){
        logger.debug("=> Read session with ID [{}]", sessionId);

        String value = redisManager.get(SerializationUtils.sessionKey(keyPrefix, sessionId));
        // 例如 Redis 调用 flushdb 清空了的所有的数据 ，读到的 session 是空的
        if (value != null){
            Session session = SerializationUtils.sessionToString(value);
            super.cache(session, session.getId());
            return session;
        }
        return null;
    }

    /**
     * 更新 session 到 Redis.
     * @param session
     */
    @Override
    protected void doUpdate(Session session){
        //如果会话过期/停止，没必要更新了
        if (session instanceof ValidatingSession && ((ValidatingSession) session).isValid()){
            logger.debug("=> Invalid session.");
            return;
        }

        logger.debug("=> Update session with ID [{}]", session.getId());

        String key = SerializationUtils.sessionKey(keyPrefix, session);
        String value = SerializationUtils.sessionFromString(session);
        redisManager.setex(key,value, timeToLiveSeconds);
    }

    /**
     * 从 Redis 删除 session，并且发布消息通知其它 Server 上的 Cache 删除 session.
     * @param session
     */
    @Override
    protected void doDelete(Session session){
        logger.debug("=> Delete session with ID [{}]", session.getId());

        redisManager.del(SerializationUtils.sessionKey(keyPrefix, session));
        // 发布消息通知其他 Server 上的 cache 删除 session
        redisManager.publish(deleteChannel, SerializationUtils.sessionIdToString(session));

        // 放在其它类里用一个 daemon 线程执行，删除 cache 中的 session
        // jedis.subscribe(new JedisPubSub() {
        //     @Override
        //     public void onMessage(String channel, String message) {
        //         // 1. deserialize message to sessionId
        //         // 2. Session session = getCachedSession(sessionId);
        //         // 3. uncache(session);
        //     }
        // }, deleteChannel);
    }

    /**
     * 取得所有有效的 session.
     * @return
     */
    @Override
    public Collection<Session> getActiveSessions(){
        logger.debug("=> Get active sessions");

        Set<String> keys = redisManager.keys(keyPrefix + "*");
        Collection<String> values = redisManager.mget(keys.toArray(new String[0]));
        List<Session> sessions = new LinkedList<Session>();
        for (String value : values) {
            sessions.add(SerializationUtils.sessionToString(value));
        }
        return sessions;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        RedisSessionDAO.logger = logger;
    }

    public static String getAuthenticatedSessionKey() {
        return AUTHENTICATED_SESSION_KEY;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public String getDeleteChannel() {
        return deleteChannel;
    }

    public void setDeleteChannel(String deleteChannel) {
        this.deleteChannel = deleteChannel;
    }

    public int getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    public void setTimeToLiveSeconds(int timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
    }

    public RedisManager getRedisManager() {
        return redisManager;
    }

    public void setRedisManager(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

}
