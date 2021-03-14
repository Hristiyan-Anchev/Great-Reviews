package greatreviews.grw.entities.basic;

import greatreviews.grw.entities.RoleEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public abstract class BaseUser extends BaseEntity  {

    @Column
    String email;

    @Column
    String password;

    @Column
    Boolean enabled;


//    todo: add image url or image entity
    @Column
    String imageURL;

    public BaseUser(){
        super();
        this.enabled = true;
        this.imageURL = "/images/haisenberg.png";
    }




    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    Set<RoleEntity> roles;

}
