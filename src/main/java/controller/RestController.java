package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by chengseas on 2016/12/4.
 */
@Controller
public class RestController {

    // 处理 GET 请求，显示 rest-form 页面
    @RequestMapping(value = "/rest", method = RequestMethod.GET)
    public String rest(){
        return "rest-form.htm";
    }

    // 处理 POST 请求
    @RequestMapping(value = "/rest", method = RequestMethod.POST)
    @ResponseBody
    public String resetPost(){
        return "{method:\"PSOT\"}";
    }

    // 处理 PUT 请求
    @RequestMapping(value="/rest", method= RequestMethod.PUT)
    @ResponseBody
    public String restPut() {
        return "{method: \"PUT\"}";
    }

    // 处理 DELETE 请求
    @RequestMapping(value="/rest", method= RequestMethod.DELETE)
    @ResponseBody
    public String restDelete() {
        return "{method: \"DELETE\"}";
    }

    @RequestMapping("/rest-ajax")
    public String restAjax() {
        return "rest-ajax.htm";
    }
}
