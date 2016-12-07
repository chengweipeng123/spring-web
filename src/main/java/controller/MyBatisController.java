package controller;

import domain.User;
import mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
}
