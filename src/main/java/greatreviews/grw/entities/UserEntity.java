package greatreviews.grw.entities;

import greatreviews.grw.entities.basic.BaseUser;
import greatreviews.grw.enums.LocationEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity extends BaseUser
//        implements UserDetails
{

    public UserEntity(String email, String password, Boolean enabled, String imageURL, Set<RoleEntity> roles, String username, LocalDate birthDate, LocationEnum location, Set<CompanyEntity> companies, Set<ReviewEntity> reviews) {
        super(email, password, enabled, imageURL, roles);
        this.username = username;
        this.birthDate = birthDate;
        this.location = location;
        this.companies = companies;
        this.reviews = reviews;
    }



    @Column
    String username;

    @Column
    LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    LocationEnum location;

    @OneToMany(mappedBy = "owner")
    Set<CompanyEntity> companies;

    @OneToMany(mappedBy = "user")
    Set<ReviewEntity> reviews;

    @OneToMany(mappedBy = "user")
    Set<ClaimTokenEntity> claimTokens;

    @ManyToMany(fetch = FetchType.EAGER)
            @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),inverseJoinColumns = @JoinColumn(name = "role_id")
            )
    Set<RoleEntity> roles;

    @ManyToMany(fetch = FetchType.EAGER)
            @JoinTable(
                    name="users_flagged_reviews",
                    joinColumns = @JoinColumn(name = "user_id"),
                    inverseJoinColumns = @JoinColumn(name = "review_id")
            )
    Set<ReviewEntity> flaggedReviews;



//    @Override
//    public String getEmail() {
//        return super.getEmail();
//    }
//
//    @Override
//    public String getPassword() {
//        return super.getPassword();
//    }
//
//    @Override
//    public Boolean getEnabled() {
//        return super.getEnabled();
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return super.getRoles();
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return super.getEnabled();
//    }


}


