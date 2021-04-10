package greatreviews.grw.controllers.basecontrollers;

import greatreviews.grw.config.authentication.CustomUser;
import greatreviews.grw.controllers.DTO.CurrentUserDTO;
import greatreviews.grw.services.interfaces.UserService;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
@ControllerAdvice()
public class BaseController {


    ModelMapper modelMapper;
    CurrentUserDTO currentUser;
    UserService userService;
    @ModelAttribute(value = "currentUser")
    public CurrentUserDTO fetchPrincipal(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        currentUser = modelMapper.map(principal, CurrentUserDTO.class);

        return currentUser;
    }


    @ExceptionHandler(Throwable.class)
    public ModelAndView globalExceptionHandler(Exception ex){
        System.out.println("** An exception was caught by global exception handler **");
        System.out.println(ex.getMessage());
        System.out.println("---------------------------------------------------------");

        return new ModelAndView("redirect:/");
    }


}
