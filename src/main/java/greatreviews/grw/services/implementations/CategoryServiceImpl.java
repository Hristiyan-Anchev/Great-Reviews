package greatreviews.grw.services.implementations;

import greatreviews.grw.controllers.views.CategoryAndSubcategoriesViewModel;
import greatreviews.grw.entities.CategoryEntity;
import greatreviews.grw.entities.SubcategoryEntity;
import greatreviews.grw.repositories.CategoryRepository;
import greatreviews.grw.repositories.SubcategoryRepository;
import greatreviews.grw.services.interfaces.CategoryService;
import greatreviews.grw.services.models.CategoryServiceModel;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository categoryRepository;
    SubcategoryRepository subcategoryRepository;
    ModelMapper modelMapper;


    public List<CategoryServiceModel>  getAllCategories(){
        return categoryRepository.getAllCategoriesWithCompanyCount();
//                modelMapper.map(categoryRepository.findAll()
//                ,new TypeToken<List<CategoryServiceModel>>(){}.getType());
    }

    @Override
    @SneakyThrows
    public String getCategoryNameById(Long categoryId) {
        Optional<CategoryEntity> targetCategory = categoryRepository.findCategoryEntityById(categoryId);
        if(targetCategory.isPresent()){
            return targetCategory.get().getName();
        }
        return "";
    }

    @Override
    public Set<CategoryAndSubcategoriesViewModel> getAllCategoriesAndSubcategories() {

        Set<CategoryAndSubcategoriesViewModel> allCategories = categoryRepository.getAllCategories();
        allCategories.forEach(cat -> {
           var subcategories =  subcategoryRepository.getSubcategoriesByCategoryId(cat.getId());
            cat.setSubcategories(subcategories);
        });

        return allCategories;
    }

    @Override
    public Optional<CategoryEntity> getCategoryEntityById(Long id) {
        return categoryRepository.findCategoryEntityById(id);

    }



}
