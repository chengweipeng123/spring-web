package controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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

    @RequestMapping("helloworld-freemarker")
    public String helloFreemarker(ModelMap model){
        // 放到 model 里的数据会在 Freemarker 模板文件里使用
        model.put("username", "Seas");
        model.put("password", "123456");
        // 当浏览器里访问 http://localhost:8080/helloworld-freemarker 时，
        // 访问的是 /WEB-INF/view/ftl/helloworld-freemarker.htm
        return "helloworld-freemarker.htm";
    }
}
