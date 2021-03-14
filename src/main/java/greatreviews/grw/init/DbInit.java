package greatreviews.grw.init;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.google.gson.Gson;
import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import greatreviews.grw.entities.*;
import greatreviews.grw.enums.LocationEnum;
import greatreviews.grw.init.dto.CategoryDTO;
import greatreviews.grw.repositories.CategoryRepository;
import greatreviews.grw.repositories.RoleRepository;
import greatreviews.grw.repositories.SubcategoryRepository;
import greatreviews.grw.repositories.UserRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import java.util.Set;
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
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    TextFileParser textFileParser;
    Gson gson;
//======================================================================================================================



//======================================================================================================================
    @Override
    public void run(String... args) throws Exception {
        initRoles();
        initCategories();
//        THIS_IS_A_TEST_METHOD();
        initDefaultUser();

//======================================================================================================================

    }

    private void initDefaultUser() {
        if(userRepository.count() == 0) {
            userRepository.saveAndFlush(new UserEntity(
                    "test@mail.com",
                    passwordEncoder.encode("passwd1"),
                    true,
                    "/images/haisenberg.png",
                    new HashSet<RoleEntity>(),
                    "test",
                    LocalDate.now(),
                    LocationEnum.GBR,
                    new HashSet<CompanyEntity>(),
                    new HashSet<ReviewEntity>()
            ));
        }

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
    List<MyReview> reviews = new ArrayList<MyReview>(
            List.of(
            new MyReview(1,1,0),
            new MyReview(2,10,1),
            new MyReview(3,40,20),
            new MyReview(4,100,40)
            )
    );



    reviews.sort((r1,r2) -> {
        return r2.confidence.compareTo(r1.confidence);
    });
    reviews.forEach(System.out::println);

    }

    public Double _confidence(Integer ups, Integer downs){
        Integer n = ups + downs;

        if(n == 0) return .0;

        Double z = 1.281551565545;
        Double p = (double)ups / n;

        Double left = p + 1/(2*n)*z*z;
        Double right = z*Math.sqrt(p*(1-p)/n + z*z/(4*n*n));
        Double under = 1 + 1/n*z*z;

        return (left + right) / under;
    }


     private class MyReview{
        public MyReview(int reviewId, int ups, int downs) {
            this.reviewId = reviewId;
            this.ups = ups;
            this.downs = downs;
            this.confidence = _confidence(this.ups,this.downs);
        }

         int reviewId;
         int ups;
         int downs;
         Double confidence;


        @Override
        public String toString() {
            return String.format("Rewiew - %d | Confidence: %f",this.reviewId,this.confidence);
        }
    }


}
