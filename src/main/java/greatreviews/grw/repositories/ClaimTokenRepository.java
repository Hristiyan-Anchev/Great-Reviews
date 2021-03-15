package greatreviews.grw.repositories;

import greatreviews.grw.entities.ClaimTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimTokenRepository extends JpaRepository<ClaimTokenEntity,Long> {


}
