package greatreviews.grw.services.implementations;

import greatreviews.grw.controllers.DTO.CurrentUserDTO;
import greatreviews.grw.controllers.DTO.SearchCompanyBinding;
import greatreviews.grw.controllers.DTO.VerificationResponseDTO;
import greatreviews.grw.entities.ClaimTokenEntity;
import greatreviews.grw.entities.CompanyEntity;
import greatreviews.grw.entities.UserEntity;
import greatreviews.grw.repositories.CompanyRepository;
import greatreviews.grw.services.interfaces.*;
import greatreviews.grw.services.models.CompanyServiceModel;
import greatreviews.grw.utilities.interfaces.Scraper;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class CompanyServiceImpl implements CompanyService {

    Scraper scraper;
    ModelMapper modelMapper;
    CompanyRepository companyRepository;
//    ClaimTokenService claimTokenService; do not call this service here (circular dependency err)
    UserService userService;
    CategoryService categoryService;
    SubcategoryService subcategoryService;
    CurrentUserDTO currentUser;

    @Override
    public Optional<CompanyServiceModel> getCompanyByWebsite(String website) {
        Optional<CompanyEntity> targetCompany = companyRepository.findCompanyEntityByWebsite(website);

        return targetCompany.map(tc -> modelMapper.map(tc, CompanyServiceModel.class));

//        return targetCompany.map(companyEntity -> modelMapper.map(companyEntity, CompanyServiceModel.class)).orElse(null);
    }

    @Override
    public Optional<CompanyServiceModel> getCompanyByEmail(String email) {
        return companyRepository.findCompanyEntityByEmail(email).map(companyEntity -> {
            return modelMapper.map(companyEntity,CompanyServiceModel.class);
        });
    }

    @Override
    @Transactional
    public void registerCompany(CompanyServiceModel companyServiceModel)  {
        // pre populate some fields by scraping

        scraper.getPage(companyServiceModel.getWebsite());
        String currentPageDescription = scraper.getCurrentPageDescription();
        String currentPageTitle = scraper.getCurrentPageTitle();

        if(currentPageTitle.isBlank() || companyRepository.findCompanyEntityByName(currentPageTitle).isPresent()){

            currentPageTitle = null;
        }

        companyServiceModel.setShortDescription(currentPageDescription);
        companyServiceModel.setName(currentPageTitle);

        //set default logo
        CompanyEntity newCompany = modelMapper.map(companyServiceModel, CompanyEntity.class);
        newCompany.setLogo("/images/big_biznis.png");
        newCompany.setIsVerified(false);



        var mainCategory = categoryService
                .getCategoryEntityById(companyServiceModel.getMainCategory());

        //set main category
       if(mainCategory.isPresent()) {
           newCompany.setCategory(mainCategory.get());

           Set<Long> serviceModelSubcategories = new LinkedHashSet<>(List.of(
                   companyServiceModel.getFirstSubcategory(),
                   companyServiceModel.getSecondSubcategory(),
                   companyServiceModel.getThirdSubcategory()
           ));


           var subcategories = newCompany.getCategory().getSubcategories()
                   .stream().filter(subcategoryEntity -> serviceModelSubcategories.contains(subcategoryEntity.getId()))
                   .collect(Collectors.toSet());
           //set subcategories
           newCompany.setSubcategories(subcategories);


           companyRepository.saveAndFlush(newCompany);
       }
       else{
           throw new IllegalArgumentException("Main category could not be found");
       }
    }

    @Override
    public Long getCompanyCountInCategory(Long categoryId) {
        return companyRepository.countAllByCategoryId(categoryId);
    }

    @Override
    public Optional<CompanyServiceModel> getCompanyById(Long id) {
        Optional<CompanyEntity> company = companyRepository.findCompanyEntityById(id);

        return  company.map(companyEntity -> {
            return modelMapper.map(companyEntity,CompanyServiceModel.class);
        });
    }

    @Override
    public CompanyEntity getCompanyEntityById(Long companyId) {
        return companyRepository.findCompanyEntityById(companyId).orElse(null);
    }

    @Override
    public Boolean isClaimInProgressForUser(Long userId, Long companyId) {

        UserEntity targetUser = userService.getUserEntityById(userId);
        //check user claim tokens, if there is a token which companyId matches and the company isVerified==false
        //then user is in process of claiming the company
        boolean isInProcessForTargetUser = targetUser.getClaimTokens().stream().anyMatch(token -> {
            return token.getCompany().getId().equals( companyId) && !token.getCompany().getIsVerified();
        });

        return isInProcessForTargetUser;
    }

    @Override
    public VerificationResponseDTO attemptVerificationFor(Long companyId, Long userId) {
        var response = new VerificationResponseDTO(false,"Something went wrong, please make sure that you have put the token in your website and is not expired!");
        Optional<CompanyEntity> targetCompany = companyRepository.findCompanyEntityById(companyId);

        targetCompany.ifPresent(tgcmp -> {
            if(!tgcmp.getIsVerified()) {
                Set<ClaimTokenEntity> userTokens = tgcmp.getClaimToken().stream().filter(token -> {
                    return token.getUser().getId().equals(userId) && !token.getIsExpired() && !token.getIsVerified();
                }).collect(Collectors.toSet());

                if (!userTokens.isEmpty()) {
                    Optional<ClaimTokenEntity> cte = userTokens.stream().findFirst();
                    //get user and compare tokens from webpage and user's
                    ClaimTokenEntity currentUserClaimToken = cte.get();

                    //get token from webpage
                    scraper.getPage(tgcmp.getWebsite());
                    String websiteVerificationToken =
                            scraper.getElementAttributeByAnotherAttribute("meta", "name", "great-reviews-one-time-domain-verification-id", "content");


                    if (        //to verify the actual  webpage uncomment this
//                            websiteVerificationToken.equals(currentUserClaimToken.getValue())

                            //to pass verification every time put TRUE - for dev purposes only
                            true
                    ) {

                        //verify token and company
                        tgcmp.setIsVerified(true);
                        tgcmp.setOwner(currentUserClaimToken.getUser());
                        currentUserClaimToken.setIsVerified(true);
                        currentUserClaimToken.setIsExpired(true);


                        //invalidate all other tokens for this company
                        tgcmp.getClaimToken().stream().filter(t-> !(t.getId().equals(currentUserClaimToken.getId()) && t.getIsVerified()))
                                .map(invalidToken -> {
                                    invalidToken.setIsExpired(true);
                                    return invalidToken;
                                });

                        companyRepository.save(tgcmp);


                        response.setVerificationSuccessful(true);
                        response.setMessage("Verification successful, you can now remove the token from the website.");
                    }
                }
            }else{
                response.setMessage("This company has already been verified");
            }
        });

        return response;
    }


    @Override
    public Set<CompanyServiceModel> getCompanyContaining(String searchString) {
        Set<CompanyEntity> companiesBySearchString = companyRepository.getCompaniesBySearchString(searchString);

        Set<CompanyServiceModel> mappedCompanies =  modelMapper.map(companiesBySearchString,
                new TypeToken<Set<CompanyServiceModel>>(){}.getType()
                );


        return mappedCompanies;
    }


    //======================================================================================================================

    private String generateClaimTokenValue(String userEmail,String companyWebsite){
        var rawData = userEmail + companyWebsite;
        var digestedData = DigestUtils.md5DigestAsHex(rawData.getBytes(StandardCharsets.UTF_8));

        return digestedData;
    }

}
