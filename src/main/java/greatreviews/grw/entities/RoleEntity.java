package greatreviews.grw.entities;

import greatreviews.grw.entities.basic.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor

@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleEntity extends BaseEntity implements GrantedAuthority {

    @Column
    String name;


    @ManyToMany(mappedBy = "roles",fetch = FetchType.EAGER)
    Set<UserEntity> users;

    @Override
    public String getAuthority() {
        return this.name;
    }
}
