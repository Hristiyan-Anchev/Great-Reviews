package greatreviews.grw.services.interfaces;

import greatreviews.grw.entities.UserMetaData;

public interface UserMetaDataService {

    void saveMetadata(UserMetaData data);

    boolean hostIsBlacklisted(String remoteHost);
}
