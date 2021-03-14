package greatreviews.grw.services.implementations;

import greatreviews.grw.config.authentication.CustomUser;
import greatreviews.grw.controllers.bindings.RegisterUserBinding;
import greatreviews.grw.entities.UserEntity;
import greatreviews.grw.repositories.RoleRepository;
import greatreviews.grw.repositories.UserRepository;
import greatreviews.grw.services.interfaces.UserService;
import greatreviews.grw.services.models.UserServiceModel;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    RoleRepository roleRepository;
    UserRepository userRepository;
    ModelMapper modelMapper;


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
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email).orElseGet(()-> null);

        //shell object
        CustomUser cu = modelMapper.map(userEntity,CustomUser.class);

        return cu;

    }
}
