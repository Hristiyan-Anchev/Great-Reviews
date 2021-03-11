package greatreviews.grw.controllers;

import greatreviews.grw.controllers.basecontrollers.BaseController;
import greatreviews.grw.controllers.bindings.RegisterUserBinding;
import greatreviews.grw.services.interfaces.UserService;
import greatreviews.grw.services.models.UserServiceModel;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
@Controller
@RequestMapping("/users")
public class UserController  {

    UserService userService;
    PasswordEncoder passwordEncoder;
    ModelMapper modelMapper;

    @GetMapping("/login")
    public ModelAndView getLoginPage(@RequestParam(name = "error", defaultValue = "")String err,  Model model) {
        var modelAndView = new ModelAndView("LoginUser");

        if(err.equals("true")){
            modelAndView.addObject("loginError",true);
        }
        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage(Model model) {
        var modelAndView = new ModelAndView("RegisterUser");
        if(!model.containsAttribute("userBinding")){
            model.addAttribute("userBinding",new RegisterUserBinding("","", LocalDate.now(),"",""));
        }

        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView registerUser(@Valid @ModelAttribute RegisterUserBinding userBinding,
                                     BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if(!passwordsMatch(userBinding)){
            bindingResult.rejectValue("","passwords.no.match","Passwords do not match");
        }

        if(!isUserUnique(userBinding)){
            bindingResult.rejectValue("","user.exists","Username is already taken");
        }

        if(!isEmailUnique(userBinding)){
            bindingResult.rejectValue("","email.exists","Email is already in use");
        }

        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("userBinding",userBinding);
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


//======================================================================================================================
    private Boolean passwordsMatch(RegisterUserBinding userBinding){
        return userBinding.getPassword().equals(userBinding.getConfirmPassword());
    }

    private Boolean isUserUnique(RegisterUserBinding userBinding){
        return userService.findByUsername(userBinding.getUsername()).isEmpty();
    }

    private Boolean isEmailUnique(RegisterUserBinding userBinding){
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
