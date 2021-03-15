package greatreviews.grw.controllers.DTO;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

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

    @Override
    public String toString() {
        return
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' ;
    }
}
