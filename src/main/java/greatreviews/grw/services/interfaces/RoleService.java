package greatreviews.grw.services.interfaces;

import greatreviews.grw.entities.RoleEntity;
import org.springframework.stereotype.Service;


public interface RoleService {

    RoleEntity findRoleById(Integer id);
}
