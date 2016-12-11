package shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;

import java.io.Serializable;

/**
 * 序列化和反序列化类
 * Created by chengseas on 2016/12/11.
 */
public class SerializationUtils {
    /**
     *  使用 sessionId 创建字符串的 key， 用来在 Reids 里作为存储 Session 的 key
     * @param prefix
     * @param sessionId
     * @return
     */
    public static String sessionKey(String prefix, Serializable sessionId){
        return prefix + sessionId;
    }

    /**
     * 使用 sessionId 创建字符串的 key， 用来在 Reids 里作为存储 Session 的 key
     * @param prefix
     * @param session
     * @return
     */
    public static String sessionKey(String prefix, Session session){
        return prefix+session.getId();
    }

    /**
     * 把 sessionId 序列化为 string，因为 Reids 的 key 和 value 必须同时为 string 或 byte[]
     * @param session
     * @return
     */
    public static String sessionIdToString(Session session){
        byte[] content = org.apache.commons.lang3.SerializationUtils.serialize(session.getId());
        return org.apache.shiro.codec.Base64.encodeToString(content);
    }

    /**
     * 反序列化 得到sessionIdS
     * @param value
     * @return
     */
    public static Serializable sessionIdFromString(String value){
        byte[] content = org.apache.shiro.codec.Base64.decode(value);
        return org.apache.commons.lang3.SerializationUtils.deserialize(content);
    }

    /**
     * 反序列化得到 session.
     * @param value
     * @return
     */
    public static Session sessionToString(String value){
        byte[] content = org.apache.shiro.codec.Base64.decode(value);
        return org.apache.commons.lang3.SerializationUtils.deserialize(content);
    }

    /**
     * 把 session 序列化为 string，因为 Redis 的 key 和 value 必须同时为 string 或者 byte[].
     * @param session
     * @return
     */
    public static String sessionFromString(Session session) {
        byte[] content = org.apache.commons.lang3.SerializationUtils.serialize((SimpleSession) session);
        return org.apache.shiro.codec.Base64.encodeToString(content);
    }
}
