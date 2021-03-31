package greatreviews.grw.controllers;

import greatreviews.grw.controllers.DTO.CensorResponseDTO;
import greatreviews.grw.controllers.DTO.CurrentUserDTO;
import greatreviews.grw.controllers.DTO.ImageUploadResponseDTO;
import greatreviews.grw.controllers.DTO.UserDisableResponseDTO;
import greatreviews.grw.controllers.bindings.RegisterUserBinding;
import greatreviews.grw.controllers.bindings.UserEditBinding;
import greatreviews.grw.controllers.views.CompanyViewModel;
import greatreviews.grw.controllers.views.ReviewViewModel;
import greatreviews.grw.controllers.views.UserViewModel;
import greatreviews.grw.services.interfaces.ClaimTokenService;
import greatreviews.grw.services.interfaces.CompanyService;
import greatreviews.grw.services.interfaces.ReviewService;
import greatreviews.grw.services.interfaces.UserService;
import greatreviews.grw.services.models.ClaimTokenServiceModel;
import greatreviews.grw.services.models.CompanyServiceModel;
import greatreviews.grw.services.models.ReviewServiceModel;
import greatreviews.grw.services.models.UserServiceModel;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
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

        if (!isUserUnique(userBinding.getUsername())) {
            bindingResult.rejectValue("", "user.exists", "Username is already taken");
        }

        if (!isEmailUnique(userBinding.getEmail())) {
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
                CurrentUserDTO currentUser = ((CurrentUserDTO) model.getAttribute("currentUser"));

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
    public ModelAndView getUserReviews(Model model, @RequestParam(name = "usrid") Long userId) {
        ModelAndView modelAndView = new ModelAndView("/review/ReviewsFromUser");

        Set<ReviewViewModel> userReviews = modelMapper.map(reviewService.getUserReviewsById(userId),
                new TypeToken<Set<ReviewViewModel>>() {
                }.getType()
        );

        modelAndView.addObject("reviews", userReviews);
        modelAndView.addObject("targetUser", userService.getUserNameById(userId));

        return modelAndView;

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/manage/companies")
    public ModelAndView getManagedCompanies(Model model) {
        CurrentUserDTO currentUser = ((CurrentUserDTO) model.getAttribute("currentUser"));

        var modelAndView = new ModelAndView("/user/manage_companies/ShowOwnedCompanies");

        Set<CompanyServiceModel> userCompanies = companyService.getCompaniesOwnedBy(currentUser.getId());

        modelAndView.addObject("companies",
                modelMapper.map(userCompanies,
                        new TypeToken<Set<CompanyViewModel>>() {
                        }.getType()
                )
        );


        return modelAndView;
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/settings")
    public ModelAndView getUserSettings(Model model, @RequestParam(name = "success", defaultValue = "") String success) {
        var modelAndView = new ModelAndView("/user/settings/UserSettingsPage");
        CurrentUserDTO currentUser = modelMapper.map(model.getAttribute("currentUser"), CurrentUserDTO.class);
        modelAndView.addObject("success", success);

        if (!model.containsAttribute("userBinding")) {
            UserServiceModel targetUserById = userService.findUserById(currentUser.getId());
            UserEditBinding userBinding = modelMapper.map(targetUserById, UserEditBinding.class);

            model.addAttribute("userBinding", userBinding);
        }

        //to preserve image through redirects
        ((UserEditBinding) model.getAttribute("userBinding")).setImageURL(
                userService.findUserById(currentUser.getId()).getImageURL()
        );
        modelAndView.addObject("userBinding", model.getAttribute("userBinding"));


        return modelAndView;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/settings")
    public ModelAndView updateUserSettings(@Valid @ModelAttribute UserEditBinding userBinding,
                                           BindingResult bindingResult,
                                           RedirectAttributes redirectAttributes, Model model
    ) {
        CurrentUserDTO currentUser = modelMapper.map(model.getAttribute("currentUser"), CurrentUserDTO.class);
        var modelAndView = new ModelAndView("redirect:/users/settings?id");


        if (userBinding.getId().equals(currentUser.getId())) {

            if (!isUserUnique(userBinding.getUsername(), List.of(currentUser.getUsername()))) {
                bindingResult.rejectValue("username", "user.exists", "Username is already taken");
            }

            if (!isEmailUnique(userBinding.getEmail(), List.of(currentUser.getEmail()))) {
                bindingResult.rejectValue("email", "email.exists", "Email is already in use");
            }

            if (bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute("userBinding", userBinding);
                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userBinding",
                        bindingResult);

                return new ModelAndView("redirect:/users/settings?success=false");
            }

            //todo update user details
            userService.updateUserDetails(userBinding);


            modelAndView.setViewName("redirect:/users/settings?success=true");
        }


        return modelAndView;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/upload/image")
    public ResponseEntity<?> uploadUserImage(Model model, MultipartFile file) {
        CurrentUserDTO currentUser = (CurrentUserDTO) model.getAttribute("currentUser");

        ImageUploadResponseDTO responseObj =
                userService.uploadUserImage(file, currentUser.getId());


        return new ResponseEntity<ImageUploadResponseDTO>(responseObj, HttpStatus.OK);

    }


    @GetMapping("/admin/panel")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView getAdminPanelPage(Model model) {
        var modelAndView = new ModelAndView("redirect:/");
        var currentUser = (CurrentUserDTO) model.getAttribute("currentUser");

        if (currentUser.getRoles().contains("ROLE_ADMIN")) {
            modelAndView.setViewName("/user/admin_templates/MainAdminControlPanel");
            Integer unpublishedReviewsCount = reviewService.getUnpublishedReviewsCount();
            Integer flaggedReviewsCount = reviewService.getFlaggedReviewsCount();
            modelAndView.addAllObjects(Map.of(
                    "unpublishedReviewsCount", unpublishedReviewsCount,
                    "flaggedReviewsCount", flaggedReviewsCount
            ));

        }

        return modelAndView;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/admin/unpublished")
    public ModelAndView getUnpublishedReviews(Model model) {
        var modelAndView = new ModelAndView("redirect:/");
        CurrentUserDTO currentUser = (CurrentUserDTO) model.getAttribute("currentUser");

        if (currentUser.getRoles().contains("ROLE_ADMIN")) {
            //get unpublished reviews
            List<ReviewServiceModel> unpublishedReviews = reviewService.getUnpublishedReviews();

            List<ReviewViewModel> mappedUnpublishedReviews =
                    modelMapper.map(unpublishedReviews, new TypeToken<List<ReviewViewModel>>() {
                    }.getType());

            modelAndView.setViewName("/user/admin_templates/UnpublishedReviewsPage");
            modelAndView.addObject("unpublishedReviews", mappedUnpublishedReviews);
        }


        return modelAndView;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/admin/flagged")
    public ModelAndView getFggedReviews(Model model) {
        CurrentUserDTO currentUser = (CurrentUserDTO) model.getAttribute("currentUser");
        var modelAndView = new ModelAndView("redirect:/");

        if (currentUser.getRoles().contains("ROLE_ADMIN")) {
            modelAndView.setViewName("/user/admin_templates/FlaggedReviewsPage");

            List<ReviewServiceModel> flaggedReviews = reviewService.getFlaggedReviews();

            List<ReviewViewModel> mappedFlaggedReviews = modelMapper.map(flaggedReviews,
                    new TypeToken<List<ReviewViewModel>>() {
                    }.getType());

            modelAndView.addObject("flaggedReviews", mappedFlaggedReviews);
        }
        return modelAndView;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/admin/censor")
    public ResponseEntity<?> toggleCensoredReview(Model model, @RequestParam(name = "id") Long reviewId) {
        CensorResponseDTO response = reviewService.toggleReviewCensorById(reviewId);


        return new ResponseEntity<CensorResponseDTO>(response, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/admin/search")
    public ModelAndView searchUsers(Model model, @RequestParam(name = "searchString") String searchString) {
        CurrentUserDTO currentUser = ((CurrentUserDTO) model.getAttribute("currentUser"));
        var modelAndView = new ModelAndView("redirect:/");

        if (currentUser.getRoles().contains("ROLE_ADMIN")) {

            List<UserServiceModel> usersResult = userService.getUserBySearchString(searchString);

            var mappedResults = modelMapper.map(usersResult,
                    new TypeToken<List<UserViewModel>>() {
                    }.getType());

            modelAndView = new ModelAndView("/user/admin_templates/UserSearchResultAdminView");
            modelAndView.addObject("searchString", searchString);
            modelAndView.addObject("users", mappedResults);
        }

        return modelAndView;
    }

    @GetMapping("/disable")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> toggleUserDisabled(Model model, @RequestParam(name = "id") Long id) {
        CurrentUserDTO currentUser = ((CurrentUserDTO) model.getAttribute("currentUser"));
        UserDisableResponseDTO userDisableResponse = new UserDisableResponseDTO(false);

        if (currentUser.getRoles().contains("ROLE_ADMIN")) {
            userDisableResponse = userService.toggleUserDisabled(id);

        }

        return new ResponseEntity<>(userDisableResponse, HttpStatus.OK);
    }

    //======================================================================================================================
    private Boolean passwordsMatch(RegisterUserBinding userBinding) {
        return userBinding.getPassword().equals(userBinding.getConfirmPassword());
    }

    private Boolean isUserUnique(String username) {
        return userService.findByUsername(username).isEmpty();
    }

    private Boolean isUserUnique(String username, List<String> exemptValues) {
        var targetUser = userService.findByUsername(username);
        Boolean result = true;

        if (targetUser.isPresent()) {
            result =
                    exemptValues.stream().anyMatch(v -> targetUser.get().getUsername().equals(v))
            ;
        }

        return result;
    }


    private Boolean isEmailUnique(String email) {
        return userService.findByEmail(email).isEmpty();
    }

    private Boolean isEmailUnique(String email, List<String> exemptValues) {
        var targetUser = userService.findByEmail(email);
        Boolean result = true;

        if (targetUser.isPresent()) {
            result = exemptValues.stream().anyMatch(v -> targetUser.get().getEmail().equals(v));
        }

        return result;
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
