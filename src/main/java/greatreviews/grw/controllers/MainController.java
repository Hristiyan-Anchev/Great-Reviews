package greatreviews.grw.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping()
public class MainController {

    @GetMapping("/")
    public ModelAndView getIndex(Model model){
        var modelAndView = new ModelAndView("index");

        return modelAndView;
    }


    @GetMapping("/home")
    public ModelAndView getHome(Model model){
        var modelAndView = new ModelAndView("index");

        return modelAndView;
    }
}
