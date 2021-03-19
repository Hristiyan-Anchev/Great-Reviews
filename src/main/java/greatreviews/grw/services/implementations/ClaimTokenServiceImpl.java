package greatreviews.grw.services.implementations;

import greatreviews.grw.controllers.DTO.CurrentUserDTO;
import greatreviews.grw.entities.ClaimTokenEntity;
import greatreviews.grw.entities.CompanyEntity;
import greatreviews.grw.entities.RoleEntity;
import greatreviews.grw.entities.UserEntity;
import greatreviews.grw.repositories.ClaimTokenRepository;
import greatreviews.grw.services.interfaces.ClaimTokenService;
import greatreviews.grw.services.interfaces.CompanyService;
import greatreviews.grw.services.interfaces.RoleService;
import greatreviews.grw.services.interfaces.UserService;
import greatreviews.grw.services.models.ClaimTokenServiceModel;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Timestamp;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClaimTokenServiceImpl implements ClaimTokenService {

    ClaimTokenRepository claimTokenRepository;
    RoleService roleService;
    CompanyService companyService;
    UserService userService;
    ModelMapper modelMapper;

    public String registerNewClaimToken(Long userId,Long companyId, ClaimTokenServiceModel claimTokenServiceMode){
        //todo: if such token value already exists and is not expired dont add a new one

            var newClaimToken = modelMapper.map(claimTokenServiceMode,ClaimTokenEntity.class);

            //add company entity to claim token
            CompanyEntity targetCompany = companyService.getCompanyEntityById(claimTokenServiceMode.getCompanyId());

            //add user entity to claim token
//          todo: check why currentUser holds no info
            UserEntity targetUser = userService.getUserEntityById(userId);

            //check if user has a pending verification for this company
        var nonExpiredUserClaimTokensForCurrentCompany =
                targetUser.getClaimTokens().stream().filter(usrToken -> {
                    return usrToken.getCompany().getId().equals(companyId) && !usrToken.getIsExpired();
                }).collect(Collectors.toSet());


            if(nonExpiredUserClaimTokensForCurrentCompany.isEmpty()) {

                //set user that is claiming
                newClaimToken.setUser(targetUser);

                //set the company for this token
                newClaimToken.setCompany(targetCompany);

                //todo make role service work with userDTO instead of entity
                roleService.setRole(targetUser.getId(),"ROLE_OWNER");

                claimTokenRepository.saveAndFlush(newClaimToken);
                return newClaimToken.getValue();
            }
        Optional<String> firstNonExpiredTokenValue =
                nonExpiredUserClaimTokensForCurrentCompany.stream().findFirst().map(ClaimTokenEntity::getValue);


        return firstNonExpiredTokenValue.orElse("");

    }
}
