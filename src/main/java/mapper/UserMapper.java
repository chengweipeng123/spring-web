package mapper;

import domain.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by chengseas on 2016/12/6.
 */

//只是定义一下接口，不需要我们提供它的实现，这个接口的实现会由 MyBatis-Spring 的框架创建。
public interface UserMapper {
    public User findUserById(int id);

    // 使用 @Param 的方式传参数
    public List<User> findUsers(@Param("offset") int offset, @Param("count") int count);

    public void updatePassword(User user);
}
