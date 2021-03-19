package greatreviews.grw.services.interfaces;

import greatreviews.grw.entities.ClaimTokenEntity;
import greatreviews.grw.services.models.ClaimTokenServiceModel;

public interface ClaimTokenService {

    String registerNewClaimToken(Long userId,Long companyId, ClaimTokenServiceModel claimToken);

}
