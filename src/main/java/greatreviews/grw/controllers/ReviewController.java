package greatreviews.grw.controllers;

import greatreviews.grw.controllers.basecontrollers.BaseController;
import greatreviews.grw.controllers.views.CompanyViewModel;
import greatreviews.grw.services.interfaces.CompanyService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/reviews")
@Controller
public class ReviewController   {
    CompanyService companyService;
    ModelMapper modelMapper;

    @GetMapping("/company")
    public ModelAndView getCompanyReviews(@RequestParam(name = "of") Long companyId) {
        var modelAndView = new ModelAndView("/company/CompanyReviewsPage");

        Optional<CompanyViewModel> companyViewModel = companyService.getCompanyById(companyId).map(companyServiceModel -> {
            return modelMapper.map(companyServiceModel, CompanyViewModel.class);
        });

        companyViewModel.ifPresent(viewModel -> modelAndView.addObject(
                "currentCompany",
                viewModel));



        return modelAndView;
    }



    @GetMapping("/company/evaluate")
    public ModelAndView getReviewForm(@RequestParam Long companyId){
        var modelAndView = new ModelAndView("");


        return modelAndView;
    }

}
