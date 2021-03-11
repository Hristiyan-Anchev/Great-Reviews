package greatreviews.grw.services.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryServiceModel {

    public CategoryServiceModel(Long id, String name, Integer companiesCount) {
        this.id = id;
        this.name = name;
        this.companiesCount = companiesCount;
    }

    public CategoryServiceModel(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    Integer companiesCount;
    Long id;
    String name;

}
