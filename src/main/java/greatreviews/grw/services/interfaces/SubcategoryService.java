package greatreviews.grw.services.interfaces;

import greatreviews.grw.entities.SubcategoryEntity;
import greatreviews.grw.services.models.CompanyServiceModel;
import greatreviews.grw.services.models.SubcategoryServiceModel;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SubcategoryService {
    List<SubcategoryServiceModel> findSubcategoriesOfMain(Long categoryId);

    Optional<SubcategoryEntity> findSubcategoryById(Long id);

    Set<CompanyServiceModel> getCompaniesInCategory(Long subcategoryId);



}
