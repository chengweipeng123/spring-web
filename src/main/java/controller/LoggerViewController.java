package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by chengseas on 2016/12/6.
 */
@Controller
public class LoggerViewController {
    // 1. 创建 logger 对象
    private static Logger logger = LoggerFactory.getLogger(LoggerViewController.class.getName());

    @RequestMapping("/logback")
    @ResponseBody
    public String logback() {
        // 2. 和 log4j 一样使用
        logger.debug("No params");

        // 3. 可以使用 {} 的方式传入参数
        logger.debug("With params: time: {}, name: {}", System.nanoTime(), "Bingo");

        return "Test logback";
    }
}
