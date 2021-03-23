package greatreviews.grw.controllers;

import greatreviews.grw.controllers.DTO.CurrentUserDTO;
import greatreviews.grw.controllers.bindings.RegisterUserBinding;
import greatreviews.grw.controllers.views.CompanyViewModel;
import greatreviews.grw.controllers.views.ReviewViewModel;
import greatreviews.grw.services.interfaces.ClaimTokenService;
import greatreviews.grw.services.interfaces.CompanyService;
import greatreviews.grw.services.interfaces.ReviewService;
import greatreviews.grw.services.interfaces.UserService;
import greatreviews.grw.services.models.ClaimTokenServiceModel;
import greatreviews.grw.services.models.CompanyServiceModel;
import greatreviews.grw.services.models.UserServiceModel;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
@Controller
@RequestMapping("/users")
public class UserController {

    UserService userService;
    CompanyService companyService;
    ReviewService reviewService;
    ClaimTokenService claimTokenService;
    PasswordEncoder passwordEncoder;
    ModelMapper modelMapper;

    @GetMapping("/login")
    public ModelAndView getLoginPage(@RequestParam(name = "error", defaultValue = "") String err, Model model) {
        var modelAndView = new ModelAndView("LoginUser");

        if (err.equals("true")) {
            modelAndView.addObject("loginError", true);
        }
        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage(Model model) {
        var modelAndView = new ModelAndView("RegisterUser");
        if (!model.containsAttribute("userBinding")) {
            model.addAttribute("userBinding", new RegisterUserBinding("", "", LocalDate.now(), "", ""));
        }

        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView registerUser(@Valid @ModelAttribute RegisterUserBinding userBinding,
                                     BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (!passwordsMatch(userBinding)) {
            bindingResult.rejectValue("", "passwords.no.match", "Passwords do not match");
        }

        if (!isUserUnique(userBinding)) {
            bindingResult.rejectValue("", "user.exists", "Username is already taken");
        }

        if (!isEmailUnique(userBinding)) {
            bindingResult.rejectValue("", "email.exists", "Email is already in use");
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("userBinding", userBinding);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userBinding",
                    bindingResult);
            return new ModelAndView("redirect:/users/register");
        }

        encodeUserBindingPasswords(userBinding);

        userService.registerUser(
                modelMapper.map(userBinding, UserServiceModel.class)
        );

        return new ModelAndView("redirect:/users/login");
    }

    @GetMapping("/claim")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView getClaimBussinessPage(Model model, @RequestParam(name = "companyId") Long companyId) {
        var modelAndView = new ModelAndView("redirect:/categories");

        Optional<CompanyServiceModel> targetCompany = companyService.getCompanyById(companyId);
        targetCompany.ifPresent((tc) -> {

            if (!tc.getIsVerified()) {
                CurrentUserDTO currentUser =((CurrentUserDTO)model.getAttribute("currentUser"));

                        String companyRawData = String.format("%s%d", tc.getWebsite(), tc.getId());
                        String currentUserRawData = currentUser.getEmail();
                        String timestamp = String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.ofTotalSeconds(0)));


                //generate company/user specific hash value
                String claimTokenHashValue = DigestUtils
                        .md5DigestAsHex(
                                (companyRawData + currentUserRawData + timestamp).getBytes(StandardCharsets.UTF_8)
                        );


                String registeredClaimTokenValue = claimTokenService.registerNewClaimToken(currentUser.getId(), companyId,
                        new ClaimTokenServiceModel(null, tc.getId(), null, claimTokenHashValue, false, false)
                );

                //add claim token to model
                modelAndView.addObject("claimToken", registeredClaimTokenValue);

                //add the html element as text so that thymeleaf can escape the markup
                modelAndView.addObject("tokenMarkup",
                        String.format("<meta name=\"great-reviews-one-time-domain-verification-id\" content= \"%s\">",
                                registeredClaimTokenValue)
                        );

                //add company view-model
                modelAndView.addObject("currentCompany", tc);


                modelAndView.setViewName("/company/ClaimCompanyPage");
            }
        });


        return modelAndView;
    }

    //todo add users reviews

    @GetMapping("/reviews")
    public ModelAndView getUserReviews(Model model, @RequestParam(name = "usrid") Long userId){
        ModelAndView modelAndView = new ModelAndView("/review/ReviewsFromUser");


        Set<ReviewViewModel> userReviews = modelMapper.map(reviewService.getUserReviewsById(userId),
            new TypeToken<Set<ReviewViewModel>>(){}.getType()
        );

        modelAndView.addObject("reviews",userReviews);
        modelAndView.addObject("targetUser",userService.getUserNameById(userId));

       return modelAndView;

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/manage/companies")
    public ModelAndView getManagedCompanies(Model model){
        CurrentUserDTO currentUser = ((CurrentUserDTO) model.getAttribute("currentUser"));

        var modelAndView = new ModelAndView("/user/manage_companies/ShowOwnedCompanies");

      Set<CompanyServiceModel> userCompanies = companyService.getCompaniesOwnedBy(currentUser.getId());

        modelAndView.addObject("companies",
              modelMapper.map(userCompanies,
                      new TypeToken<Set<CompanyViewModel>>(){}.getType()
                      )
              );


        return modelAndView;
    }


    //======================================================================================================================
    private Boolean passwordsMatch(RegisterUserBinding userBinding) {
        return userBinding.getPassword().equals(userBinding.getConfirmPassword());
    }

    private Boolean isUserUnique(RegisterUserBinding userBinding) {
        return userService.findByUsername(userBinding.getUsername()).isEmpty();
    }

    private Boolean isEmailUnique(RegisterUserBinding userBinding) {
        return userService.findByEmail(userBinding.getEmail()).isEmpty();
    }

    private void encodeUserBindingPasswords(RegisterUserBinding userBinding) {
        userBinding.setPassword(
                passwordEncoder.encode(userBinding.getPassword())
        );

        userBinding.setConfirmPassword(
                passwordEncoder.encode(userBinding.getConfirmPassword())
        );
    }
}
