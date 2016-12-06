package controller;

import domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * Created by chengseas on 2016/12/6.
 */
@Controller
public class ValidateController {
    @RequestMapping("/validate-user")
    @ResponseBody
    public String validateUser(@ModelAttribute @Valid User user, BindingResult result){
        System.out.println(result.hasErrors());
        if (result.hasErrors()){
            return result.getFieldError().toString();
        }
        return "名字: " + user.getUsername() + ", Password: " + user.getPassword();
    }
}
