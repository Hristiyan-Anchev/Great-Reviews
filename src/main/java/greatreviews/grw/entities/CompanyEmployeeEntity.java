package greatreviews.grw.entities;


import greatreviews.grw.entities.basic.BaseUser;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyEmployeeEntity extends BaseUser {

    @Column
    String firstName;

    @Column
    String lastName;

    @Column
    String jobTitle;

    @Column
    String phoneNumber;



}
