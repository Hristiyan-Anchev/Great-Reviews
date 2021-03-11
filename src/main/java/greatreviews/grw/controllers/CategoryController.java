package greatreviews.grw.controllers;

import greatreviews.grw.controllers.basecontrollers.BaseController;
import greatreviews.grw.controllers.views.CategoryViewModel;
import greatreviews.grw.controllers.views.SubcategoryViewModel;
import greatreviews.grw.repositories.CategoryRepository;
import greatreviews.grw.services.interfaces.CategoryService;
import greatreviews.grw.services.interfaces.CompanyService;
import greatreviews.grw.services.interfaces.SubcategoryService;
import greatreviews.grw.services.models.CategoryServiceModel;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryController {

    CategoryService categoryService;
    SubcategoryService subcategoryService;
    CompanyService companyService;

    ModelMapper modelMapper;


    @GetMapping("/categories")
    public ModelAndView getAllCategoriesPage(Model model){

        var modelAndView = new ModelAndView("Categories");

        List<CategoryViewModel> categories = modelMapper.map(
                categoryService.getAllCategories(),
                new TypeToken<List<CategoryViewModel>>(){}.getType()
        );

        modelAndView.addObject("categories",categories);
        modelAndView.addObject("activeCategory","");

        return modelAndView;
    }

    @GetMapping("/categories/subcategories")
    public ModelAndView getSubcategories(@RequestParam( defaultValue = "", name = "of") Long categoryId, Model model){
        String activeCategory = categoryService.getCategoryNameById(categoryId);

        List<CategoryViewModel> categories = modelMapper.map(
                categoryService.getAllCategories(),
                new TypeToken<List<CategoryViewModel>>(){}.getType()
        );

        List<SubcategoryViewModel> subcategories = modelMapper.map(
                subcategoryService.findSubcategoriesOfMain(categoryId),
                new TypeToken<List<SubcategoryViewModel>>(){}.getType()
        );

        var modelAndView = new ModelAndView("Subcategories",
                Map.of(
                        "categories",categories,
                        "subcategories",subcategories,
                        "activeCategory",activeCategory
                ));


        return modelAndView;
    }
}
