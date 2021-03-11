package greatreviews.grw.repositories;

import greatreviews.grw.controllers.views.CategoryViewModel;
import greatreviews.grw.controllers.views.SubcategoryViewModel;
import greatreviews.grw.entities.CategoryEntity;
import greatreviews.grw.entities.CompanyEntity;
import greatreviews.grw.entities.SubcategoryEntity;
import greatreviews.grw.services.models.SubcategoryServiceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SubcategoryRepository extends JpaRepository<SubcategoryEntity, Long> {

    List<SubcategoryEntity>  findAllByCategoryId(Long id);

    @Query("SELECT new greatreviews.grw.controllers.views.SubcategoryViewModel(sub.id,sub.name,sub.companies.size) FROM SubcategoryEntity sub WHERE sub.category.id = ?1")
    Set<SubcategoryViewModel> getSubcategoriesByCategoryId(Long id);

    Optional<SubcategoryEntity> findSubcategoryEntityById(Long id);

    @Query("SELECT new greatreviews.grw.services.models.SubcategoryServiceModel(sub.id,sub.name,sub.companies.size) FROM SubcategoryEntity sub")
    Set<SubcategoryServiceModel> getAllSubcategoriesWithCompanyCount();

    @Query("SELECT sub.companies FROM SubcategoryEntity sub WHERE sub.id = ?1")
    Set<CompanyEntity> getCompaniesBySubcategoryId(Long id);
}
