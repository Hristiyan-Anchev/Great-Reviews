package greatreviews.grw.services.interfaces;

import greatreviews.grw.controllers.DTO.ImageUploadResponseDTO;
import greatreviews.grw.controllers.bindings.UserEditBinding;
import greatreviews.grw.controllers.views.ReviewViewModel;
import greatreviews.grw.entities.CompanyEntity;
import greatreviews.grw.entities.UserEntity;
import greatreviews.grw.services.models.UserServiceModel;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;

@Service
public interface UserService  extends UserDetailsService {
    Optional<UserServiceModel> findByUsername(String username);
    @Transactional
    Optional<UserServiceModel> findByEmail(String email);
    void registerUser(UserServiceModel userServiceModel);

    UserEntity getUserEntityById(Long id);


    String getUserNameById(Long userId);

    UserServiceModel findUserById(Long userId);

    void updateUserDetails(UserEditBinding userBinding);

    ImageUploadResponseDTO uploadUserImage(MultipartFile multipartFile, Long id);
}
