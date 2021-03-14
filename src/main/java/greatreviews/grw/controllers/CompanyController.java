package greatreviews.grw.controllers;
import greatreviews.grw.controllers.basecontrollers.BaseController;
import greatreviews.grw.controllers.bindings.AddCompanyBinding;
import greatreviews.grw.controllers.views.CategoryAndSubcategoriesViewModel;
import greatreviews.grw.controllers.views.CompanyViewModel;
import greatreviews.grw.repositories.SubcategoryRepository;
import greatreviews.grw.services.interfaces.CategoryService;
import greatreviews.grw.services.interfaces.CompanyService;
import greatreviews.grw.services.interfaces.SubcategoryService;
import greatreviews.grw.services.models.CompanyServiceModel;
import greatreviews.grw.utilities.interfaces.Scraper;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
@Controller
@RequestMapping("/companies")
public class CompanyController {

    ModelMapper modelMapper;
    CompanyService companyService;
    CategoryService categoryService;
    SubcategoryService subcategoryService;
    Scraper scraper;

    SubcategoryRepository subcategoryRepository;

    @PreAuthorize(value = "isAuthenticated()")
    @GetMapping("/add")
    public ModelAndView getAddCompanyView(Model model) {
        var modelAndView = new ModelAndView("/company/AddCompany");

        if (!model.containsAttribute("companyBinding")) {
            model.addAttribute("companyBinding", new AddCompanyBinding("", "", ""));
        }

        //TODO: refactor this view model in the future to make binding easier
        Set<CategoryAndSubcategoriesViewModel> allCategoriesAndSubcategories =
                categoryService.getAllCategoriesAndSubcategories();

        modelAndView.addObject("companyBinding", model.getAttribute("companyBinding"));
        modelAndView.addObject("categories", allCategoriesAndSubcategories);
        modelAndView.addObject("subcategoriesMap",
                allCategoriesAndSubcategories
                        .stream()
                        .collect(
                                Collectors
                                        .toMap(CategoryAndSubcategoriesViewModel::getId,
                                                CategoryAndSubcategoriesViewModel::getSubcategories))
        );

        return modelAndView;
    }

    @PreAuthorize(value = "isAuthenticated()")
    @PostMapping("/add")
    public ModelAndView registerCompany(@Valid @ModelAttribute AddCompanyBinding companyBinding, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        var modelAndView = new ModelAndView("redirect:/");




        //check if company is in the database already
        //check by website
        if (companyService.getCompanyByWebsite(companyBinding.getWebsite()).isPresent()) {
            bindingResult.rejectValue("website", "company.exists", "This website is already in our database");
        }

        //check by email
        if (companyService.getCompanyByEmail(companyBinding.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "company.exists", "This email is already in our database");
        }

        //check if mainCategory exists in database
        if(!categoryService.getCategoryEntityById(companyBinding.getMainCategory()).isPresent()){
            bindingResult.rejectValue("mainCategory","category.not.exists","The category does not exist in the database");
        }



        //check if someone tried to shove the same subcategory more than once ...
        var subcategoriesCollection = new Long[]{
                companyBinding.getFirstSubcategory(),
                companyBinding.getSecondSubcategory(),
                companyBinding.getThirdSubcategory()};
        subcategoriesCollection = Arrays.stream(subcategoriesCollection).filter(subc -> subc != 0).toArray(Long[]::new);

        Integer initialSize = subcategoriesCollection.length;

        Integer filteredSize = new LinkedHashSet<Long>(List.of(subcategoriesCollection)).size();



        if (initialSize != filteredSize ) {
            bindingResult.rejectValue("", "problem.choosing.subcategory", "There was a problem choosing subcategories");
        }

        if(filteredSize < 1){
            bindingResult.rejectValue("","no.subcategory.chosen","Please choose a subcategory");
        }

        // if NOT in database --
//        check for form errors

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("companyBinding", companyBinding);
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.companyBinding", bindingResult
            );

            modelAndView.setViewName("redirect:/companies/add");
            return modelAndView;
        }

        //if validation is successful
        //map to service model and pass to service to add to database
            companyService.registerCompany(modelMapper.map(companyBinding, CompanyServiceModel.class));

        return modelAndView;
    }

    @GetMapping("/show")
    public ModelAndView showCompaniesInCertainCategory(@RequestParam(name = "in") Long id){
        var modelAndView = new ModelAndView("ShowCompaniesInSubcategory");


        var companies = subcategoryService
                             .getCompaniesInCategory(id);

        //put subcategory name in modelAndView
        subcategoryService.findSubcategoryById(id).ifPresent(subcategoryEntity -> {
            modelAndView.addObject("currentSubcategory",subcategoryEntity.getName());
        });

        Set<CompanyViewModel> companyViewModels =
             modelMapper.map(companies,
                     new TypeToken<Set<CompanyViewModel>>(){}.getType());



        modelAndView.addObject("companies",companyViewModels);



        return modelAndView;
    }





    //todo: refactor this to a custom exception
    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView illegalCategoryHandler(){
        return new ModelAndView("redirect:/companies/add?error=true");
}

}
