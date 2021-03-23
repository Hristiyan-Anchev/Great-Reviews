package greatreviews.grw.entities;

import greatreviews.grw.entities.basic.BaseEntity;
import greatreviews.grw.enums.PatternEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.Set;


@Entity
@Table(name = "companies")
@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@DynamicUpdate
public class CompanyEntity extends BaseEntity {

    public CompanyEntity() {
        this.isVerified = false;
    }

    @Column(nullable = false, unique = true)
    @Pattern(regexp = PatternEnum.Constants.VALID_FQDN_PATTERN)
    String website;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = PatternEnum.Constants.EMAIL_PATTERN)
    String email;

    @Column(nullable = true, unique = true)
    String name;

    @Column(nullable = true)
    String address;

    @Column(nullable = true, unique = true)
    String phone;

    @Column(nullable = true)
    String logo;

    @Column(nullable = true)
    String shortDescription;

    @Column
    Boolean isVerified;

    @OneToMany(mappedBy = "company")
    Set<ClaimTokenEntity> claimToken;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    UserEntity owner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    CategoryEntity category;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "companies_subcategories",
            joinColumns = @JoinColumn(name = "company_id"), inverseJoinColumns = @JoinColumn(name = "subcategory_id")
    )
    Set<SubcategoryEntity> subcategories;

    @OneToMany(mappedBy = "company")
    Set<ReviewEntity> reviews;


}
