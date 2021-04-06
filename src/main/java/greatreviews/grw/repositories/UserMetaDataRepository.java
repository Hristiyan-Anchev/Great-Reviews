package greatreviews.grw.repositories;

import greatreviews.grw.entities.UserMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserMetaDataRepository extends JpaRepository<UserMetaData,Long> {
    Optional<UserMetaData> findUserMetaDataByRemoteHost(String remoteHost);
}
