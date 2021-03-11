package greatreviews.grw.services.interfaces;

import greatreviews.grw.controllers.views.CategoryAndSubcategoriesViewModel;
import greatreviews.grw.entities.CategoryEntity;
import greatreviews.grw.entities.SubcategoryEntity;
import greatreviews.grw.services.models.CategoryServiceModel;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CategoryService {

    List<CategoryServiceModel> getAllCategories();
    String getCategoryNameById(Long categoryId);
    Set<CategoryAndSubcategoriesViewModel> getAllCategoriesAndSubcategories();
    Optional<CategoryEntity> getCategoryEntityById(Long id);


}
