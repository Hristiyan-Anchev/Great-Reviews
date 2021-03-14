package greatreviews.grw.services.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubcategoryServiceModel {

    public SubcategoryServiceModel(Long id, String name, Integer companiesCount) {
        this.id = id;
        this.name = name;
        this.companiesCount = companiesCount;
    }

    public SubcategoryServiceModel(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    Integer companiesCount;
    Long id;
    String name;
    Boolean isDeleted;

}
