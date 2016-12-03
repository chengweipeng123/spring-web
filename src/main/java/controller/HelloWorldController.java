package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by chengseas on 2016/12/3.
 */
@Controller
public class HelloWorldController {

    @RequestMapping("/helloworld-springmvc")
    public String helloWorld(){
        return "helloworld-springmvc.jsp";
    }
}
