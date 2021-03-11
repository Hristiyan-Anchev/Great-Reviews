package greatreviews.grw.controllers.views;

import greatreviews.grw.entities.SubcategoryEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@NoArgsConstructor
//@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryAndSubcategoriesViewModel {

    public CategoryAndSubcategoriesViewModel(Long id, String name) {
        this.id = id;
        this.name = name;
        this.subcategories = null;

    }


    Long id;
    String name;
    Set<SubcategoryViewModel> subcategories;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<SubcategoryViewModel> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(Set<SubcategoryViewModel> subcategories) {
        this.subcategories = subcategories;
    }
}
