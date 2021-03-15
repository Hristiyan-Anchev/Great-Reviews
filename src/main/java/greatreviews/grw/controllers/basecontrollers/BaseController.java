package greatreviews.grw.controllers.basecontrollers;

import greatreviews.grw.controllers.DTO.CurrentUserDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;


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
    CurrentUserDTO currentUser;


    @ModelAttribute(value = "currentUser")
    public CurrentUserDTO fetchPrincipal(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

         currentUser = modelMapper.map(principal, CurrentUserDTO.class);

        return currentUser;
    }



}
