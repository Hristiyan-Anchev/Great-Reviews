package greatreviews.grw.entities;

import greatreviews.grw.entities.basic.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity()
@Table(name = "claim_tokens")
@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClaimTokenEntity extends BaseEntity {

    public ClaimTokenEntity() {
        this.isExpired = false;
    }

    @Column(unique = true,nullable = false)
    String value;

    @Column(nullable = false)
    Boolean isExpired;

    @Column(nullable = false)
    Boolean isVerified;

    @ManyToOne(fetch = FetchType.EAGER)
            @JoinColumn(
                    name = "user_id",
                    referencedColumnName = "id"
            )
    UserEntity user;

    @ManyToOne(fetch = FetchType.EAGER)
    CompanyEntity company;


}
