package greatreviews.grw.entities;

import greatreviews.grw.entities.basic.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;


@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class CompanyEntity extends BaseEntity {

    @Column
    String website;

    @Column
    String companyName;

    @Column
    String companyAddress;

    @Column
    String companyPhone;

    @Column
    String companyEmail;

    @Column
    String companyLogo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id",referencedColumnName = "id")
    UserEntity owner;





    //todo


}
