package greatreviews.grw.services.interfaces;

import greatreviews.grw.entities.ClaimTokenEntity;
import greatreviews.grw.services.models.ClaimTokenServiceModel;

public interface ClaimTokenService {

    ClaimTokenEntity registerNewClaimToken(ClaimTokenServiceModel claimToken);
}
