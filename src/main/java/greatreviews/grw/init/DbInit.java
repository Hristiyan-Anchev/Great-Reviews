package greatreviews.grw.init;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.google.gson.Gson;
import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import greatreviews.grw.entities.CategoryEntity;
import greatreviews.grw.entities.RoleEntity;
import greatreviews.grw.entities.SubcategoryEntity;
import greatreviews.grw.init.dto.CategoryDTO;
import greatreviews.grw.repositories.CategoryRepository;
import greatreviews.grw.repositories.RoleRepository;
import greatreviews.grw.repositories.SubcategoryRepository;
import greatreviews.grw.utilities.TextFileParser;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class DbInit implements CommandLineRunner {
    public static final String SEED_DIR = "src/main/resources/dbseed/";

    public static final Logger LOGGER = LoggerFactory.getLogger(DbInit.class);

    RoleRepository roleRepository;
    CategoryRepository categoryRepository;
    SubcategoryRepository subcategoryRepository;

    TextFileParser textFileParser;
    Gson gson;
    //======================================================================================================================



//======================================================================================================================
    @Override
    public void run(String... args) throws Exception {
        initRoles();
        initCategories();
//        THIS_IS_A_TEST_METHOD();

//======================================================================================================================

    }


    protected void initRoles(){
        if(roleRepository.count() == 0) {
            var adminRole = new RoleEntity();
            adminRole.setName("ROLE_ADMIN");

            var userRole = new RoleEntity();
            userRole.setName("ROLE_USER");

            var ownerRole = new RoleEntity();
            ownerRole.setName("ROLE_OWNER");

            var employeeRole = new RoleEntity();
            employeeRole.setName("ROLE_EMPLOYEE");

            var companyRole = new RoleEntity();
            companyRole.setName("ROLE_COMPANY");

            roleRepository.saveAll(List.of(
                    adminRole, userRole, ownerRole, employeeRole
            ));
        }
    }

    @Transactional
    protected void initCategories(){
        if(categoryRepository.count() == 0){

        String jsonContent = textFileParser.parseFileAt(SEED_DIR + "categories.json");
        List<CategoryDTO> importedCategories = gson.fromJson(jsonContent,new TypeToken<List<CategoryDTO>>(){}.getType());
        List<CategoryEntity> categoryEntities = importedCategories.stream().map(ic -> {
            var category = new CategoryEntity();
            category.setName(ic.getCategory());

            categoryRepository.save(category);

            List<SubcategoryEntity> subcategories = ic.getSubcategories().stream().map(subCat -> {
//                var subcategory = new SubcategoryEntity();
//                subcategory.setName(subCat);
//                return subcategory;
                return new SubcategoryEntity(subCat, category);
            }).collect(Collectors.toList());

                subcategoryRepository.saveAll(subcategories);
                //subcategoryRepository.flush();
            category.setSubcategories(new HashSet<>(subcategories));

            return category;
        }).collect(Collectors.toList());

        categoryRepository.saveAll(categoryEntities);

        System.out.println();

        }
    }


    protected void THIS_IS_A_TEST_METHOD() throws IOException, InterruptedException {
        Document document = Jsoup.connect("http://www.apple.com").get();
        Elements elements = document.getElementsByAttributeValueContaining("name", "description");

        if(elements.isEmpty()){
             elements = document.getElementsByAttributeValueContaining("name", "Description");
        }else




        System.out.println();

    }

}
