package greatreviews.grw.services.implementations;

import greatreviews.grw.entities.RoleEntity;
import greatreviews.grw.entities.UserEntity;
import greatreviews.grw.repositories.RoleRepository;
import greatreviews.grw.repositories.UserRepository;
import greatreviews.grw.services.interfaces.RoleService;
import greatreviews.grw.services.interfaces.UserService;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;
    UserService userService;
    UserRepository userRepository;

    @Override
    public RoleEntity findRoleById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Override
    public void setRole(Long  userId,String roleName) {
        UserEntity targetUser = userService.getUserEntityById(userId);

        Optional<RoleEntity> roleEntityOpt = roleRepository.findByName(roleName);

        RoleEntity roleEntity = roleEntityOpt.orElseGet(RoleEntity::new);


        Set<UserEntity> ownerRoleUsers = roleEntity.getUsers();
        if(targetUser.getRoles() == null){
            targetUser.setRoles(Set.of(roleEntity));
        }else{
            targetUser.getRoles().add(roleEntity);
        }

        userRepository.saveAndFlush(targetUser);
    }
}
