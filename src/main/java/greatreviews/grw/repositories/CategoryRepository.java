package greatreviews.grw.repositories;

import greatreviews.grw.controllers.views.CategoryAndSubcategoriesViewModel;
import greatreviews.grw.controllers.views.CategoryViewModel;
import greatreviews.grw.controllers.views.SubcategoryViewModel;
import greatreviews.grw.entities.CategoryEntity;
import greatreviews.grw.entities.SubcategoryEntity;
import greatreviews.grw.services.models.CategoryServiceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hibernate.loader.Loader.SELECT;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Optional<CategoryEntity> findCategoryEntityById(Long id);

    @Query("SELECT new greatreviews.grw.controllers.views.CategoryAndSubcategoriesViewModel(c.id, c.name) FROM CategoryEntity c")
    Set<CategoryAndSubcategoriesViewModel> getAllCategories();

    @Query("SELECT new greatreviews.grw.services.models.CategoryServiceModel(c.id,c.name,c.companies.size) FROM CategoryEntity c")
    List<CategoryServiceModel> getAllCategoriesWithCompanyCount();









}
