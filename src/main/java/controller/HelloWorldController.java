package controller;

import domain.Address;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by chengseas on 2016/12/3.
 */
@Controller
public class HelloWorldController {

    @RequestMapping("/helloworld-springmvc")
    public String helloWorld(){
        return "helloworld-springmvc.jsp";
    }

    @RequestMapping("helloworld-freemarker")
    public String helloFreemarker(ModelMap model){
        // 放到 model 里的数据会在 Freemarker 模板文件里使用
        model.put("username", "Seas");
        model.put("password", "123456");
        // 当浏览器里访问 http://localhost:8080/helloworld-freemarker 时，
        // 访问的是 /WEB-INF/view/ftl/helloworld-freemarker.htm
        return "helloworld-freemarker.htm";
    }

    @RequestMapping("/ajax")
    @ResponseBody // 处理 AJAX 请求，返回响应的内容，而不是 View Name，需要在方法的前面加上 @ResponseBody。
    public String handleAjax(){
        return "{username:\"seas\",password:\"123456\"}";
    }

    //不直接返回 Json 字符串，而是返回一个对象，SpringMVC 自动的把对象转换为 Json，需要 Jackson。
    @RequestMapping(value = "/json",method = RequestMethod.GET)
    @ResponseBody
    public Address getAddress(){
        Address address = new Address();
        address.setId(1);
        address.setStreet("桃园");
        return address;
    }
}
