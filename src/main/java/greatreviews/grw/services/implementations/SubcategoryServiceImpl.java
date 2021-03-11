package greatreviews.grw.services.implementations;

import greatreviews.grw.entities.CompanyEntity;
import greatreviews.grw.entities.SubcategoryEntity;
import greatreviews.grw.repositories.SubcategoryRepository;
import greatreviews.grw.services.interfaces.SubcategoryService;
import greatreviews.grw.services.models.CompanyServiceModel;
import greatreviews.grw.services.models.SubcategoryServiceModel;
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
public class SubcategoryServiceImpl implements SubcategoryService {

    SubcategoryRepository subcategoryRepository;
    ModelMapper modelMapper;

    @Override
    public List<SubcategoryServiceModel> findSubcategoriesOfMain(Long categoryId) {
        var targetSubcategories = subcategoryRepository.getSubcategoriesByCategoryId(categoryId);

        return modelMapper.map(targetSubcategories, new TypeToken<List<SubcategoryServiceModel>>(){}.getType());
    }

    @Override
    public Optional<SubcategoryEntity> findSubcategoryById(Long id) {
        return subcategoryRepository.findSubcategoryEntityById(id);
    }


    @Override
    public Set<CompanyServiceModel> getCompaniesInCategory(Long subcategoryId) {
        Set<CompanyEntity> targetCompanies = subcategoryRepository.getCompaniesBySubcategoryId(subcategoryId);


        Set<CompanyServiceModel> mapped = modelMapper.map(
                targetCompanies,
                new TypeToken<Set<CompanyServiceModel>>(){}.getType()
                );

        return mapped;

    }


}
