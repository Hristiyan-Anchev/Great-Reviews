package greatreviews.grw.controllers;

import greatreviews.grw.controllers.views.CompanyViewModel;
import greatreviews.grw.controllers.DTO.CurrentUserDTO;
import greatreviews.grw.controllers.DTO.AddReviewDTO;
import greatreviews.grw.controllers.views.ReviewViewModel;
import greatreviews.grw.services.interfaces.CompanyService;
import greatreviews.grw.services.interfaces.ReviewService;
import greatreviews.grw.services.models.ReviewServiceModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
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
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/reviews")
@Controller
public class ReviewController {

    CompanyService companyService;
    ReviewService reviewService;
    ModelMapper modelMapper;

    @GetMapping("/company")
    public ModelAndView getCompanyReviews(@RequestParam(name = "of") Long companyId) {
        var modelAndView = new ModelAndView("redirect:/categories");

        Optional<CompanyViewModel> companyViewModel = companyService.getCompanyById(companyId).map(companyServiceModel -> {
            return modelMapper.map(companyServiceModel, CompanyViewModel.class);
        });

        companyViewModel.ifPresent(viewModel -> {
            var reviewsStats = getCompanyReviewsStats(viewModel.getId());
            viewModel.setReviewsCount(reviewsStats[0]);
            viewModel.setUpVotesCount(reviewsStats[1]);
            viewModel.setDownVotesCount(reviewsStats[2]);
            modelAndView.addObject("currentCompany", viewModel);

            Set<ReviewServiceModel> companyReviews =
                    reviewService.getCompanyReviews(viewModel.getId());

            Set<ReviewViewModel> reviewsViewModel =
                    modelMapper.map(companyReviews, new TypeToken<Set<ReviewViewModel>>(){}.getType());

        modelAndView.addObject("reviews",reviewsViewModel);

        modelAndView.setViewName("/company/CompanyReviewsPage");
        });


        return modelAndView;
    }


    @PreAuthorize(value = "isAuthenticated()")
    @GetMapping("/company/evaluate")
    public ModelAndView getReviewForm(Model model, @RequestParam(name = "cid") Long companyId){
        var modelAndView = new ModelAndView("redirect:/categories");

        if(!model.containsAttribute("review")){
            modelAndView.addObject("review", new AddReviewDTO("","",""));
        }

        //adding the current company stats
        Optional<CompanyViewModel> currentCompany =
                companyService.getCompanyById(companyId).map(companyServiceModel -> modelMapper.map(companyServiceModel,CompanyViewModel.class));

        //passing company view model to template
        currentCompany.ifPresent(companyViewModel -> {
            var reviewsStats = getCompanyReviewsStats(companyViewModel.getId());
            companyViewModel.setReviewsCount(reviewsStats[0]);
            companyViewModel.setUpVotesCount(reviewsStats[1]);
            companyViewModel.setDownVotesCount(reviewsStats[2]);
            modelAndView.addObject("currentCompany", companyViewModel);
            modelAndView.setViewName("/review/ReviewForm");
        });


        return modelAndView;
    }


    @PreAuthorize(value = "isAuthenticated()")
    @PostMapping("/company/evaluate")
    public ModelAndView addCompanyReview(@Valid @ModelAttribute AddReviewDTO review,
                                         BindingResult bindingResult,
                                         RedirectAttributes redirectAttributes,
                                         Model model,
                                         @RequestParam(name = "cid") Long companyId){

        var modelAndView = new ModelAndView("redirect:/reviews/company?of=" + companyId);



            if(bindingResult.hasErrors()){
                redirectAttributes.addFlashAttribute("review",review);
                redirectAttributes.addFlashAttribute("allErrors",bindingResult.getAllErrors());
                redirectAttributes.addFlashAttribute(
                        "org.springframework.validation.BindingResult.review",
                        bindingResult
                );

                modelAndView.setViewName("redirect:/reviews/company/evaluate?cid=" + companyId);
                return modelAndView;
            }

            ReviewServiceModel reviewServiceModel = modelMapper.map(review,ReviewServiceModel.class);


            Optional<CurrentUserDTO> currentUser = Optional.of(
                    (model.getAttribute("currentUser"))
            ).map((obj)->((CurrentUserDTO)obj));
           Long userId = currentUser.map(CurrentUserDTO::getId).orElse(0L);


         reviewService.addReview(reviewServiceModel,userId,companyId);

        return modelAndView;
    }





//======================================================================================================================
    private Long[] getCompanyReviewsStats(Long companyId){
        Long reviewsCount = reviewService.getCompanyReviewsCount(companyId);
        Long upVotesCount = reviewService.getCompanyUpVotesCount(companyId);
        Long downVotesCount = reviewService.getCompanyDownVotesCount(companyId);

        return new Long[]{
                reviewsCount,upVotesCount,downVotesCount
        };
    }

}
