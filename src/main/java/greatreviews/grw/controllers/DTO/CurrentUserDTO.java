package greatreviews.grw.controllers.DTO;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class CurrentUserDTO {

    Long id;
    String username;
    String email;
    String imageURL;
    Boolean hasCompanies;
    List<Long> ownedCompanies;
    List<String> roles;

    @Override
    public String toString() {
        return
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' ;
    }
}
