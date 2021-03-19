package greatreviews.grw.services.interfaces;

import greatreviews.grw.entities.RoleEntity;
import greatreviews.grw.entities.UserEntity;
import org.springframework.stereotype.Service;


public interface RoleService {

    RoleEntity findRoleById(Long id);

    void setRole(Long userId, String roleName);
}
