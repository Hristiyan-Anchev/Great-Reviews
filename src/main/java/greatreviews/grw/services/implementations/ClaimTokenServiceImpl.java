package greatreviews.grw.services.implementations;

import greatreviews.grw.controllers.DTO.CurrentUserDTO;
import greatreviews.grw.entities.ClaimTokenEntity;
import greatreviews.grw.entities.CompanyEntity;
import greatreviews.grw.entities.UserEntity;
import greatreviews.grw.repositories.ClaimTokenRepository;
import greatreviews.grw.services.interfaces.ClaimTokenService;
import greatreviews.grw.services.interfaces.CompanyService;
import greatreviews.grw.services.interfaces.UserService;
import greatreviews.grw.services.models.ClaimTokenServiceModel;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClaimTokenServiceImpl implements ClaimTokenService {

    ClaimTokenRepository claimTokenRepository;
    CurrentUserDTO currentUser;
    CompanyService companyService;
    UserService userService;
    ModelMapper modelMapper;

    public ClaimTokenEntity registerNewClaimToken(ClaimTokenServiceModel claimTokenServiceMode){
            var newClaimToken = modelMapper.map(claimTokenServiceMode,ClaimTokenEntity.class);
            //add company entity to claim token
            CompanyEntity targetCompany = companyService.getCompanyEntityById(claimTokenServiceMode.getCompanyId());

            //add user entity to claim token
            UserEntity targetUser = userService.getUserEntityById(currentUser.getId());

            //set user that is claiming
            newClaimToken.setUser(targetUser);

            //set the company for this token
            newClaimToken.setCompany(targetCompany);





            claimTokenRepository.saveAndFlush(newClaimToken);

            return newClaimToken;
    }
}
