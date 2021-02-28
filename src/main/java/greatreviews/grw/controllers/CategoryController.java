package greatreviews.grw.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CategoryController {

    @GetMapping("/categories")
    public ModelAndView getAllCategoriesPage(Model model){
        var modelAndView = new ModelAndView("Categories");

        return modelAndView;
    }

    @GetMapping("/categories/subcategories")
    public ModelAndView getSubcategories(Model model){
        var modelAndView = new ModelAndView("Subcategories");

        return modelAndView;
    }
}
