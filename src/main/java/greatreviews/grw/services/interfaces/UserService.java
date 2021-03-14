package greatreviews.grw.services.interfaces;

import greatreviews.grw.services.models.UserServiceModel;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.Optional;

@Service
public interface UserService  extends UserDetailsService {
    Optional<UserServiceModel> findByUsername(String username);
    @Transactional
    Optional<UserServiceModel> findByEmail(String email);
    void registerUser(UserServiceModel userServiceModel);

}
