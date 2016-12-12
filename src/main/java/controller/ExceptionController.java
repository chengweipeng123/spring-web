package controller;

import exception.ApplicationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by chengseas on 2016/12/12.
 */
@Controller
public class ExceptionController {

    @RequestMapping("/ex1")
    @ResponseBody
    public String testEx1() {
        if (true) {
            throw new RuntimeException("Runtime Exception Test");
        }

        return "{name:\"Cathy one\"}";
    }

    @RequestMapping("/ex2")
    @ResponseBody
    public String testEx2() {
        if (true) {
            ApplicationException ex = new ApplicationException("Application Exception Test");
            ex.addAttribute("detail", "Encrypted!"); // 其他错误信息
            throw ex;
        }

        return "{name: \"Kitty two\"}";
    }
}
