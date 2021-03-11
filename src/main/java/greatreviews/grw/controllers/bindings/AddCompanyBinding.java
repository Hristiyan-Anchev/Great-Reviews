package greatreviews.grw.controllers.bindings;

import greatreviews.grw.controllers.views.CategoryViewModel;
import greatreviews.grw.controllers.views.SubcategoryViewModel;
import greatreviews.grw.enums.PatternEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.annotation.Nullable;
import javax.validation.constraints.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddCompanyBinding {

    public AddCompanyBinding(@Pattern(regexp = PatternEnum.Constants.EMAIL_PATTERN, message = "Malformed email") String email, @Pattern(regexp = PatternEnum.Constants.VALID_URL_PATTERN, message = "Malformed URL") String website, String address) {
        this.email = email;
        this.website = website;
        this.address = address;
    }

    @Pattern(regexp = PatternEnum.Constants.EMAIL_PATTERN,message = "Malformed email")
    String email;

    @Pattern(regexp = PatternEnum.Constants.VALID_FQDN_PATTERN, message = "Malformed domain")
    String website;


    String address;

    @Min(value = 1,message = "Enter a valid category")
    @NotNull(message = "Main category cannot be empty")
    Long mainCategory;

    @Min(value = 0,message = "Enter a valid category")
    Long firstSubcategory;

    @Min(value = 0,message = "Enter a valid category")
    Long secondSubcategory;

    @Min(value = 0,message = "Enter a valid category")
    Long thirdSubcategory;

    //populated in controller by scraping this.website
    String shortDescription;

    //populated in controller by scraping this.website
    String name;

    List<CategoryViewModel> allCategories;
    List<SubcategoryViewModel> allSubcategories;

}
