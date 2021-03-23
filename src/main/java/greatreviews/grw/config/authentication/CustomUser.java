package greatreviews.grw.config.authentication;

import greatreviews.grw.entities.RoleEntity;
import greatreviews.grw.enums.LocationEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter

@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomUser extends User {

    /*
    *
    * This is used as a shell object in order used to access additional user properties from the security context
    *
    * */



    public CustomUser(){
        // UGLY!!... dont do that
//        we need a default no args constructor in order to instantiate this class
//        the values passed to super() are then re-assigned with new ones
        super("null","null",new HashSet<GrantedAuthority>(List.of(new RoleEntity())));
    }


    public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities, Long id, LocalDateTime created, Boolean isDeleted, String email, String password1, Boolean enabled, String imageURL, String username1, LocalDate birthDate, LocationEnum location) {
        super(username, password, authorities);
        this.id = id;
        this.created = created;
        this.isDeleted = isDeleted;
        this.email = email;
        this.password = password1;
        this.enabled = enabled;
        this.imageURL = imageURL;
        this.username = username1;
        this.birthDate = birthDate;
        this.location = location;
    }

    public CustomUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, Long id, LocalDateTime created, Boolean isDeleted, String email, String password1, Boolean enabled1, String imageURL, String username1, LocalDate birthDate, LocationEnum location,Boolean hasCompanies) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
        this.created = created;
        this.isDeleted = isDeleted;
        this.email = email;
        this.password = password1;
        this.enabled = enabled1;
        this.imageURL = imageURL;
        this.username = username1;
        this.birthDate = birthDate;
        this.location = location;
        this.hasCompanies = hasCompanies;
    }


    Long id;

    LocalDateTime created;

    Boolean isDeleted;

    String email;

    String password;

    Boolean enabled;

    String imageURL;

    String username;

    LocalDate birthDate;

    LocationEnum location;

    Boolean hasCompanies;

    Set<RoleEntity> roles;

    List<Long> ownedCompanies;


    @Override
    public Collection<GrantedAuthority> getAuthorities() {

        return new ArrayList<GrantedAuthority>(this.roles);
    }
}
