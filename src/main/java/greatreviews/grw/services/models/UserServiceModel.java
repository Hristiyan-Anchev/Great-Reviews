package greatreviews.grw.services.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceModel {

    String email;
    String username;
    LocalDate birthDate;
    String password;

}
