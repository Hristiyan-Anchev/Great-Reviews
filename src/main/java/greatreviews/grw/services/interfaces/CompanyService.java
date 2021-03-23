package greatreviews.grw.services.interfaces;

import greatreviews.grw.controllers.DTO.ImageUploadResponseDTO;
import greatreviews.grw.controllers.DTO.VerificationResponseDTO;
import greatreviews.grw.controllers.bindings.CompanySettingsBinding;
import greatreviews.grw.entities.CompanyEntity;
import greatreviews.grw.services.models.CompanyServiceModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;

public interface CompanyService {

    Optional<CompanyServiceModel> getCompanyByWebsite(String website);

    Optional<CompanyServiceModel> getCompanyByEmail(String email);

    void registerCompany(CompanyServiceModel companyServiceModel);

    Long getCompanyCountInCategory(Long categoryId);

    Optional<CompanyServiceModel> getCompanyById(Long id);

    CompanyEntity getCompanyEntityById(Long companyId);

    Boolean isClaimInProgressForUser(Long userId, Long companyId);

    VerificationResponseDTO attemptVerificationFor(Long companyId, Long id);

    Set<CompanyServiceModel> getCompanyContaining(String searchString);

    Set<CompanyServiceModel> getCompaniesOwnedBy(Long id);

    void updateCompanyDetails(CompanySettingsBinding companySettingsBinding);

    ImageUploadResponseDTO uploadLogo(Long companyId, MultipartFile file);
}
