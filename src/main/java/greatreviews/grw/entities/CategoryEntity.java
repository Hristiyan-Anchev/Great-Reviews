package greatreviews.grw.entities;

import greatreviews.grw.entities.basic.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryEntity extends BaseEntity {

    @Column
    String name;

    @OneToMany(mappedBy = "category")
    Set<SubcategoryEntity> subcategories;

    @OneToMany(mappedBy = "category")
    Set<CompanyEntity> companies;

}
