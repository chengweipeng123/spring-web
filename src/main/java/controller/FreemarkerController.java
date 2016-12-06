package controller;

import domain.Animal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by chengseas on 2016/12/6.
 */
@Controller
public class FreemarkerController {

    @RequestMapping("/freemarker-syntax")
    public String syntax(ModelMap map) {
        map.addAttribute("username", "Alice");
        List<Animal> animals = new LinkedList<Animal>();
        animals.add(new Animal("Dog", 10));
        animals.add(new Animal("Pig", 20));
        animals.add(new Animal("Cat", 30));
        animals.add(new Animal("Tiger", 40));
        map.addAttribute("animals", animals);
        return "freemarker-syntax.htm";
    }
}
