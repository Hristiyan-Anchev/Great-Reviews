package greatreviews.grw.entities;

import greatreviews.grw.entities.basic.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "subcategories")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubcategoryEntity extends BaseEntity {

    public SubcategoryEntity(String name, CategoryEntity category) {
        this.name = name;
        this.category = category;
    }

    @Column
    String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id",referencedColumnName = "id",nullable = true)
    CategoryEntity category;

    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "subcategories")
    Set<CompanyEntity> companies;


}
