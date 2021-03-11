package greatreviews.grw.controllers.views;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor

@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryViewModel {
    public CategoryViewModel(Long id, String name, Integer companiesCount) {
        this.id = id;
        this.name = name;
        this.companiesCount = companiesCount;
    }

    public CategoryViewModel(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    Integer companiesCount;
    Long id;
    String name;
}
