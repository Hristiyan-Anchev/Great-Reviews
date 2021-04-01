package greatreviews.grw.repositories;

import greatreviews.grw.entities.ReviewEntity;
import greatreviews.grw.entities.UserEntity;
import greatreviews.grw.services.models.UserServiceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
   Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE u.email LIKE CONCAT('%',?1,'%') OR u.username LIKE CONCAT('%',?1,'%')")
    List<UserEntity> findUsersBySearchString(String search);


}
