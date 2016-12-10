package controller;

import domain.User;
import mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by chengseas on 2016/12/6.
 */
@Controller
public class MyBatisController {

    @Autowired
    private UserMapper userMapper;

    private static Logger logger = LoggerFactory.getLogger(LoggerViewController.class.getName());

    @RequestMapping("/user-mybatis/{userId}")  // http://localhost:8080/user-mybatis/1
    @ResponseBody
    public String findUser(@PathVariable Integer userId){
        User user = userMapper.findUserById(userId);
        logger.error("测试 No params");

        List<User> users = userMapper.findUsers(1,2);
        System.out.println(users);

        return user.toString();
    }

    @Transactional // 开启事务
//    @Transactional(
//            propagation = Propagation.REQUIRED, //事务传播行为
//            isolation = Isolation.READ_COMMITTED, //事务隔离级别
//            readOnly = true,  //事务读写性
//            timeout = 10,  //超时时间，s
//            rollbackFor = {SQLException.class}, //一组异常类，遇到时回滚
//            rollbackForClassName = {}, //一组异常类名，遇到时回滚
//            noRollbackForClassName = {"Exception"}, //一组异常类名，遇到时回滚
//            noRollbackFor = {}  //一组异常类，遇到时不回滚
//            )
    @RequestMapping(value = "/user/{userId}/password/{password}")
    @ResponseBody
    public String updatePassword(@PathVariable int userId, @PathVariable String password){
        User user = new User();
        user.setId(userId);
        user.setPassword(password);
        userMapper.updatePassword(user);
        if (password.length() < 5){
//            默认任何 RuntimeException 将触发事务回滚，但是任何 checked Exception 将不触发事务回滚
            throw new RuntimeException("Password's length is less than 5.");
        }
        return "Success";
    }
}
