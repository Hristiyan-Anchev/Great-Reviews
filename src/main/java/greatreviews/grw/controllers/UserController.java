package greatreviews.grw.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping()
public class UserController {

    @GetMapping("/users/login")
    public ModelAndView getLoginPage(Model model){
        var modelAndView = new ModelAndView("LoginUser");

        return modelAndView;
    }

    @GetMapping("/users/register")
    public ModelAndView getRegisterPage(Model model){
        var modelAndView = new ModelAndView("RegisterUser");

        return modelAndView;
    }
}
