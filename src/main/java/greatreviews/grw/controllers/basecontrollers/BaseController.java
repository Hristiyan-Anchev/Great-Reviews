package greatreviews.grw.controllers.basecontrollers;

import greatreviews.grw.controllers.views.CurrentUserViewModel;
import greatreviews.grw.entities.UserEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)

@Component
@ControllerAdvice
public class BaseController {

    @Autowired
    ModelMapper modelMapper;


    @ModelAttribute(value = "currentUser")
    public CurrentUserViewModel fetchPrincipal(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CurrentUserViewModel currentUser = modelMapper.map(principal,CurrentUserViewModel.class);


        return currentUser;
    }
}
