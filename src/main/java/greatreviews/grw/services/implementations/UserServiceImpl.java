package greatreviews.grw.services.implementations;

import greatreviews.grw.controllers.bindings.RegisterUserBinding;
import greatreviews.grw.services.interfaces.UserService;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    public void saveUser(RegisterUserBinding userBinding){

    }

}
