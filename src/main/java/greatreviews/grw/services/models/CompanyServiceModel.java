package greatreviews.grw.services.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyServiceModel {

    Long id;
    String email;
    String website;
    String address;
    String logo;
    Long mainCategory;
    Long firstSubcategory;
    Long secondSubcategory;
    Long thirdSubcategory;

    String name;
    String shortDescription;
}
