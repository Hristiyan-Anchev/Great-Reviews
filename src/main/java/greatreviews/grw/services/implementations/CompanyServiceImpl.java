package greatreviews.grw.services.implementations;

import greatreviews.grw.entities.CategoryEntity;
import greatreviews.grw.entities.CompanyEntity;
import greatreviews.grw.entities.SubcategoryEntity;
import greatreviews.grw.repositories.CategoryRepository;
import greatreviews.grw.repositories.CompanyRepository;
import greatreviews.grw.repositories.SubcategoryRepository;
import greatreviews.grw.services.interfaces.CategoryService;
import greatreviews.grw.services.interfaces.CompanyService;
import greatreviews.grw.services.interfaces.SubcategoryService;
import greatreviews.grw.services.models.CompanyServiceModel;
import greatreviews.grw.utilities.interfaces.Scraper;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    CategoryService categoryService;
    SubcategoryService subcategoryService;

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

        CompanyEntity newCompany = modelMapper.map(companyServiceModel, CompanyEntity.class);

        var mainCategory = categoryService
                .getCategoryEntityById(companyServiceModel.getMainCategory());

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


}
