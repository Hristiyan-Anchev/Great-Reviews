package greatreviews.grw.controllers;

import greatreviews.grw.controllers.basecontrollers.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@RequestMapping
@Controller
public class BlogController {

    @GetMapping("/blog")
    public ModelAndView getBlogPage(Model model){
        var modelAndView = new ModelAndView("Blog");

        return modelAndView;
    }

    @GetMapping("/blog/post")
    public ModelAndView getBlogPost(Model model){
        var modelAndView = new ModelAndView("BlogPost");

        return modelAndView;
    }
}
