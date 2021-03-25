package greatreviews.grw.services.implementations;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import greatreviews.grw.config.authentication.CustomUser;
import greatreviews.grw.controllers.DTO.ImageUploadResponseDTO;
import greatreviews.grw.controllers.bindings.RegisterUserBinding;
import greatreviews.grw.controllers.bindings.UserEditBinding;
import greatreviews.grw.controllers.views.ReviewViewModel;
import greatreviews.grw.entities.CompanyEntity;
import greatreviews.grw.entities.ReviewEntity;
import greatreviews.grw.entities.UserEntity;
import greatreviews.grw.repositories.RoleRepository;
import greatreviews.grw.repositories.UserRepository;
import greatreviews.grw.services.interfaces.CompanyService;
import greatreviews.grw.services.interfaces.ReviewService;
import greatreviews.grw.services.interfaces.UserService;
import greatreviews.grw.services.models.UserServiceModel;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.tika.Tika;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    public static final String TRANSFORMATION_PARAMS = "w_300,h_300,c_fill";

    RoleRepository roleRepository;
    UserRepository userRepository;
    ReviewService reviewService;

    ModelMapper modelMapper;
    Tika tika;
    Cloudinary cloudinary;


    @Override
    public Optional<UserServiceModel> findByUsername(String username) {
        Optional<UserEntity> targetUser = userRepository.findByUsername(username);

        return   targetUser.map(userEntity -> modelMapper.map(userEntity,UserServiceModel.class));
    }

    @Override
    public Optional<UserServiceModel> findByEmail(String email) {

        return  userRepository.findByEmail(email).map(userEntity -> modelMapper.map(userEntity,UserServiceModel.class));
    }


    @Override
    public void registerUser(UserServiceModel userServiceModel) {
        UserEntity newUser = modelMapper.map(userServiceModel,UserEntity.class);
//        var targetRole = roleRepository.findByName("USER");
//        role.setUsers(Set.of(newUser));

        newUser.setRoles(Set.of(roleRepository.findByName("ROLE_USER").get()));

        UserEntity save = userRepository.saveAndFlush(newUser);

    }

    @Override
    public UserEntity getUserEntityById(Long id) {

        return userRepository.findById(id).orElse(null);
    }

    @Override
    public String getUserNameById(Long userId) {
        var targetUser = userRepository.findById(userId);

        return targetUser.map(tu->{
            return tu.getUsername();
        }).orElse("");
    }

    @Override
    public UserServiceModel findUserById(Long userId) {
        UserEntity targetUser = userRepository.findById(userId).orElse(null);
        var mappedUser = modelMapper.map(targetUser,UserServiceModel.class);



        return mappedUser;
    }

    @Override
    public void updateUserDetails(UserEditBinding userBinding) {

            Optional<UserEntity> userToUpdate = userRepository.findById(userBinding.getId());

            userToUpdate.ifPresent(user ->{
                user.setEmail(
                        userBinding.getEmail()
                );

                user.setUsername(
                        userBinding.getUsername()
                );

                user.setBirthDate(
                        userBinding.getBirthDate()
                );

                userRepository.saveAndFlush(user);
            });



    }

    @Override
    public ImageUploadResponseDTO uploadUserImage(MultipartFile file, Long id) {
        var responseObj =  new ImageUploadResponseDTO("Upload failed",false,"");


        try {
            if(file != null && !file.isEmpty() && tika.detect(file.getInputStream()).contains("image/")){

                //upload the new picture
                var uploadResponse = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                var resourceSecureURL = uploadResponse.get("secure_url").toString();

                var targetUser = userRepository.findById(id);

                if(targetUser.isPresent()){
                    var user = targetUser.get();

                    //remove old user image
                    cloudinary.uploader().destroy(extractPublicId(user.getImageURL()),ObjectUtils.emptyMap());

                    //set the new URL to user
                    user.setImageURL(
                            insertTransformation(TRANSFORMATION_PARAMS,resourceSecureURL)
                    );

                    userRepository.saveAndFlush(user);

                    responseObj = new ImageUploadResponseDTO("Upload successful",true,user.getImageURL());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return responseObj;
        }

    return responseObj;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email).orElseGet(()-> null);
        //get Authorities
        //shell object
        CustomUser cu = modelMapper.map(userEntity,CustomUser.class);
//        cu.setHasCompanies(userEntity.getCompanies().size() > 0);

        return cu;

    }


    //======================================================================================================================

    public String extractPublicId(String resourceUrl){
        //todo: Util calss candidate

        int lastSlashIdx = resourceUrl.lastIndexOf("/");
        int fileExtensionDotIdx = resourceUrl.lastIndexOf(".");
        String result = resourceUrl.substring(lastSlashIdx + 1,fileExtensionDotIdx);
        System.out.println(result);
        return  result;
    }

    private String insertTransformation(String transformation, String resourceUrl){
        //todo: Util calss candidate

        StringBuilder sb = new StringBuilder();

        List<String> tokens = Arrays.asList(resourceUrl.split("/"));

        tokens = tokens.stream().map(token -> {
            var newToken = token;
            if(token.equals("upload")){
                newToken = token + ("/" + transformation);
            }

            return newToken;
        }).collect(Collectors.toList());

        String result = String.join("/", tokens);
        System.out.println(result);
        return result;
    }


}
