package greatreviews.grw.controllers;
import greatreviews.grw.controllers.DTO.CurrentUserDTO;
import greatreviews.grw.controllers.DTO.ImageUploadResponseDTO;
import greatreviews.grw.controllers.bindings.CompanySettingsBinding;
import greatreviews.grw.controllers.bindings.SearchCompanyBinding;
import greatreviews.grw.controllers.DTO.VerificationRequestDTO;
import greatreviews.grw.controllers.DTO.VerificationResponseDTO;
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
import org.apache.commons.io.FileUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
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



        if (!initialSize.equals( filteredSize )) {
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


    //todo
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/verify")
    public ResponseEntity<VerificationResponseDTO> verifyCompany(Model model, @RequestBody VerificationRequestDTO requestBody){

    CurrentUserDTO currentUser = (CurrentUserDTO)model.getAttribute("currentUser");


    VerificationResponseDTO vrdto =
            companyService.attemptVerificationFor(requestBody.getCompanyId(),currentUser.getId());

        HttpHeaders headers = new HttpHeaders();

        if(!vrdto.getVerificationSuccessful()){
            return new ResponseEntity<VerificationResponseDTO>(vrdto,HttpStatus.OK);
        }


    return  new ResponseEntity<VerificationResponseDTO>(vrdto,HttpStatus.OK);
}


    @GetMapping("/search")
    public ModelAndView showSearchResult(@ModelAttribute SearchCompanyBinding searchString){
        var modelAndView = new ModelAndView("/company/CompanySearchResult");
        Set<CompanyServiceModel> companies = companyService.getCompanyContaining(searchString.getSearch());

        Set<CompanyViewModel> mappedCompanies = modelMapper.map(companies,
                new TypeToken<Set<CompanyViewModel>>() {
                }.getType()
        );


        modelAndView
                .addObject("companies",mappedCompanies);
        modelAndView
                .addObject("searchString",searchString.getSearch());


        return modelAndView;
    }

    @GetMapping("/settings")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView getCompanySettingsPage(Model model,
                                               @RequestParam(name = "id") Long companyId,
                                               @RequestParam(name = "success",defaultValue = "") String isSuccessful){

        var modelAndView = new ModelAndView("/company/CompanySettingsPage");
        modelAndView.addObject("updateSuccessful", isSuccessful);

        var companyServiceModel = companyService.getCompanyById(companyId).orElse(new CompanyServiceModel());
        var companyBinding = modelMapper.map(companyServiceModel, CompanySettingsBinding.class);


        if (!model.containsAttribute("companyBinding")) {
            model.addAttribute("companyBinding", companyBinding);
        }

        //to preserve logo through redirects
        ((CompanySettingsBinding)model.getAttribute("companyBinding")).
                setLogo(companyBinding.getLogo());

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

    @PostMapping("/settings")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView updateCompany(@Valid @ModelAttribute(name="companyBinding") CompanySettingsBinding companySettingsBinding, BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes
                                      ){
            var modelAndView = new ModelAndView("redirect:/users/manage/companies");


                if(canAccessCompany(companySettingsBinding.getId())){

                    CompanyServiceModel companyById = companyService.getCompanyById(companySettingsBinding.getId()).orElse(new CompanyServiceModel());

                    //website is not allowed to change make sure you set the same website every time
                    companySettingsBinding.setWebsite(companyById.getWebsite());


                    //check if email already exists
                    var targetCompany = companyService.getCompanyByEmail(companySettingsBinding.getEmail()).orElse(new CompanyServiceModel());
                    if(targetCompany.getId() != null && !companySettingsBinding.getId().equals(targetCompany.getId())){
                        bindingResult.rejectValue("email","email.exists","Such email is already in use by another company");
                    }

                    //check if mainCategory exists in database
                    if(!categoryService.getCategoryEntityById(companySettingsBinding.getMainCategory()).isPresent()){
                        bindingResult.rejectValue("mainCategory","category.not.exists","The category does not exist in the database");
                    }

                    //check if someone tried to shove the same subcategory more than once ...
                    var subcategoriesCollection = new Long[]{
                            companySettingsBinding.getFirstSubcategory(),
                            companySettingsBinding.getSecondSubcategory(),
                            companySettingsBinding.getThirdSubcategory()};
                    subcategoriesCollection = Arrays.stream(subcategoriesCollection).filter(subc -> subc != 0).toArray(Long[]::new);

                    Integer initialSize = subcategoriesCollection.length;

                    Integer filteredSize = new LinkedHashSet<Long>(List.of(subcategoriesCollection)).size();


                    if (!initialSize.equals( filteredSize )) {
                        bindingResult.rejectValue("", "problem.choosing.subcategory", "There was a problem choosing subcategories");
                    }

                    if(filteredSize < 1){
                        bindingResult.rejectValue("","no.subcategory.chosen","Please choose a subcategory");
                    }


                    //check for default form errors
                    if(bindingResult.hasErrors()){
                        redirectAttributes.addFlashAttribute("companyBinding",companySettingsBinding);
                        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.companyBinding",bindingResult);
                        modelAndView.setViewName(String.format("redirect:/companies/settings?id=%d&success=false",companySettingsBinding.getId()));
                        return modelAndView;
                    }

                    companyService.updateCompanyDetails(companySettingsBinding);

                    modelAndView.setViewName(String.format("redirect:/companies/settings?id=%d&success=true",companySettingsBinding.getId()));
                }

        return modelAndView;
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/settings/upload/logo")
    public ResponseEntity<?> uploadCompanyImage(MultipartFile file, @RequestParam(name="id")Long companyId){

        ImageUploadResponseDTO imageUploadResponseDTO = companyService.uploadLogo(companyId, file);

        return new ResponseEntity<ImageUploadResponseDTO>(imageUploadResponseDTO,HttpStatus.OK);
    }


//======================================================================================================================
    //todo: refactor this to a custom exception
    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView illegalCategoryHandler(){
        return new ModelAndView("redirect:/companies/add?error=true");
}

    private Boolean canAccessCompany(Long companyId){
        CurrentUserDTO currentUser = modelMapper.map( SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
                CurrentUserDTO.class);
        return currentUser.getOwnedCompanies().stream().anyMatch(cid -> cid.equals(companyId));

    }

}
