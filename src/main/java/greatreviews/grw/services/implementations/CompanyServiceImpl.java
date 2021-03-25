package greatreviews.grw.services.implementations;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import greatreviews.grw.controllers.DTO.CurrentUserDTO;
import greatreviews.grw.controllers.DTO.ImageUploadResponseDTO;
import greatreviews.grw.controllers.DTO.VerificationResponseDTO;
import greatreviews.grw.controllers.bindings.CompanySettingsBinding;
import greatreviews.grw.entities.*;
import greatreviews.grw.repositories.CompanyRepository;
import greatreviews.grw.services.interfaces.*;
import greatreviews.grw.services.models.CompanyServiceModel;
import greatreviews.grw.utilities.interfaces.Scraper;
import lombok.*;
import lombok.experimental.FieldDefaults;

import org.apache.tika.Tika;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class CompanyServiceImpl implements CompanyService {
        public static final String TRANSFORMATION_PARAMS = "w_300,h_300,c_fill";
        Scraper scraper;
        ModelMapper modelMapper;
        CompanyRepository companyRepository;
        RoleService roleService;
    //    ClaimTokenService claimTokenService; do not call this service here (circular dependency err)
        UserService userService;
        CategoryService categoryService;
        SubcategoryService subcategoryService;
        CurrentUserDTO currentUser;
        Tika tika;

        Cloudinary cloudinary;


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

                            companyRepository.saveAndFlush(tgcmp);

                            //todo make role service work with userDTO instead of entity
                            //add company owner role to user
                            roleService.setRole(currentUserClaimToken.getUser().getId(),"ROLE_OWNER");

                            //reset authentication object
                            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                                    auth.getPrincipal(),
                                    auth.getCredentials(),
                                    currentUserClaimToken.getUser().getRoles()
                            );

                            SecurityContextHolder.getContext().setAuthentication(newAuth);
                            //


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

        @Override
        public Set<CompanyServiceModel> getCompaniesOwnedBy(Long id) {
            Set<CompanyEntity> companyEntitiesByOwnerId = companyRepository.findCompanyEntitiesByOwnerId(id);

            return modelMapper.map(companyEntitiesByOwnerId,
                    new TypeToken<Set<CompanyServiceModel>>(){}.getType()
                    );
        }

        @Override
        public void updateCompanyDetails(CompanySettingsBinding companySettingsBinding) {
            Optional<CompanyEntity> companyEntityById =
                    companyRepository.findCompanyEntityById(companySettingsBinding.getId());

            companyEntityById.ifPresent((companyEntity -> {

                companyEntity.setEmail(
                        companySettingsBinding.getEmail()
                );

                companyEntity.setAddress(
                        companySettingsBinding.getAddress()
                );

                companyEntity.setName(
                        companySettingsBinding.getName()
                );

                companyEntity.setShortDescription(
                        companySettingsBinding.getShortDescription()
                );

                Optional<CategoryEntity> categoryEntityById = categoryService.getCategoryEntityById(companySettingsBinding.getMainCategory());

                categoryEntityById.ifPresent(categoryEntity -> {
                    companyEntity.setCategory(categoryEntity);

                    Set<SubcategoryEntity> newSubcategories = new HashSet<SubcategoryEntity>();

                    var firstCategory = categoryEntity.getSubcategories().stream().filter(currentSubcategory -> currentSubcategory.getId().equals(companySettingsBinding.getFirstSubcategory())).collect(Collectors.toList());
                    var secondCategory = categoryEntity.getSubcategories().stream().filter(currentSubcategory -> currentSubcategory.getId().equals(companySettingsBinding.getSecondSubcategory())).collect(Collectors.toList());
                    var thirdCategory = categoryEntity.getSubcategories().stream().filter(currentSubcategory -> currentSubcategory.getId().equals(companySettingsBinding.getThirdSubcategory())).collect(Collectors.toList());

                    if(firstCategory.size() != 0){
                        newSubcategories.add(firstCategory.get(0));
                    }

                    if(secondCategory.size() != 0){
                        newSubcategories.add(secondCategory.get(0));
                    }

                    if(thirdCategory.size() != 0){
                        newSubcategories.add(thirdCategory.get(0));
                    }

                    companyEntity.setSubcategories(newSubcategories);

                    companyRepository.saveAndFlush(companyEntity);
                });

            }));




        }

        @Override
        public ImageUploadResponseDTO uploadLogo(Long companyId, MultipartFile file) {
                var responseObject = new ImageUploadResponseDTO("Something went wrong ... check your file again",false,"");

            if(file != null && !file.isEmpty()  ) {


                try {
                    String detect = tika.detect(file.getInputStream());

                    if(!detect.startsWith("image/")) return responseObject;

                    //upload image
                    var uploadResponse = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                    var resourceSecureURL = uploadResponse.get("secure_url").toString();


                    Optional<CompanyEntity> companyEntityById = companyRepository.findCompanyEntityById(companyId);
                    companyEntityById.ifPresent(ce -> {

                        // delete old resource
                        //todo: remove this try-catch
                        try {
                            //remove previous image
                            cloudinary.uploader().destroy(extractPublicId(ce.getLogo()),ObjectUtils.emptyMap());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String transformedLogo = insertTransformation(TRANSFORMATION_PARAMS,resourceSecureURL);
                        ce.setLogo(transformedLogo);
                        companyRepository.saveAndFlush(ce);
                    });

                    responseObject.setUploadSuccessful(true);
                    responseObject.setMsg("Image upload successful");
                    responseObject.setNewUrl(insertTransformation(TRANSFORMATION_PARAMS,resourceSecureURL));


                } catch (Exception e) {
                    responseObject=  new ImageUploadResponseDTO("Image not uploaded, an error occured", false,"");
                }
            }

            return responseObject;

        }


        //======================================================================================================================

        private String generateClaimTokenValue(String userEmail,String companyWebsite){
            var rawData = userEmail + companyWebsite;
            var digestedData = DigestUtils.md5DigestAsHex(rawData.getBytes(StandardCharsets.UTF_8));

            return digestedData;
        }

        public String extractPublicId(String resourceUrl){
            // todo: Util class candidate
            int lastSlashIdx = resourceUrl.lastIndexOf("/");
            int fileExtensionDotIdx = resourceUrl.lastIndexOf(".");
            String result = resourceUrl.substring(lastSlashIdx + 1,fileExtensionDotIdx);
            System.out.println(result);
            return  result;
        }

        private String insertTransformation(String transformation, String resourceUrl){
            //todo: Util class candidate

            StringBuilder sb = new StringBuilder();

            List<String> tokens = Arrays.asList(resourceUrl.split("/"));

            tokens = tokens.stream().map(token -> {
                var newToken = token;
                if(token.equals("upload")){
                     newToken = token + ("/" + transformation);
                }

                return newToken;
            }).collect(Collectors.toList());

            String result = String.join("/", tokens);
            System.out.println(result);
            return result;
        }

}
