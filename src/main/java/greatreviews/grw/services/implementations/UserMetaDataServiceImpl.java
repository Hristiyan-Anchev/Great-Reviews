package greatreviews.grw.services.implementations;

import greatreviews.grw.entities.UserMetaData;
import greatreviews.grw.repositories.UserMetaDataRepository;
import greatreviews.grw.services.interfaces.UserMetaDataService;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserMetaDataServiceImpl implements UserMetaDataService {
    UserMetaDataRepository metadataRepository;


    @Override
    public void saveMetadata(UserMetaData data) {
            metadataRepository.saveAndFlush(data);
    }

    @Override
    public boolean hostIsBlacklisted(String remoteHost) {
        Optional<UserMetaData> userMetaDataByRemoteHostOptional = metadataRepository.findUserMetaDataByRemoteHost(remoteHost);

        if (userMetaDataByRemoteHostOptional.isPresent()) {
            UserMetaData userMetaData = userMetaDataByRemoteHostOptional.get();
            if (!userMetaData.getUser().getEnabled() ) {
                return true;
            }
        }

        return false;
    }

}
