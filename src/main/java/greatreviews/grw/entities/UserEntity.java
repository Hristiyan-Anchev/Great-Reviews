package greatreviews.grw.entities;

import greatreviews.grw.entities.basic.BaseUser;
import greatreviews.grw.enums.LocationEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity extends BaseUser {


    @Column
    String username;

    @Column
    LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    LocationEnum location;

    @OneToMany(mappedBy = "owner")
    Set<CompanyEntity> companies;



}


