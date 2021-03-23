package greatreviews.grw.controllers.bindings;

import greatreviews.grw.controllers.views.CategoryViewModel;
import greatreviews.grw.controllers.views.SubcategoryViewModel;
import greatreviews.grw.enums.PatternEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanySettingsBinding {

    public CompanySettingsBinding(@Pattern(regexp = PatternEnum.Constants.EMAIL_PATTERN, message = "Malformed email") String email, @Pattern(regexp = PatternEnum.Constants.VALID_FQDN_PATTERN, message = "Malformed domain") String website, String address) {
        this.email = email;
        this.website = website;
        this.address = address;
    }

    @Min(1)
    Long id;

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
    @Length(max = 5000)
    String shortDescription;

    //populated in controller by scraping this.website
    String name;

    String logo;

    List<CategoryViewModel> allCategories;
    List<SubcategoryViewModel> allSubcategories;

}
