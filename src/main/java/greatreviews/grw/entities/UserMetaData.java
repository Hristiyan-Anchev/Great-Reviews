package greatreviews.grw.entities;

import greatreviews.grw.entities.basic.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Table(name = "user_metadata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserMetaData extends BaseEntity {

    @Column
    String remoteHost;

    @OneToOne(mappedBy = "metaData",fetch = FetchType.EAGER)
    UserEntity user;

}
