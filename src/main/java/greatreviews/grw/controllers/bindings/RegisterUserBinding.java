package greatreviews.grw.controllers.bindings;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class RegisterUserBinding {

    String email;
    String username;
    LocalDate birthDate;
    String password;
    String confirmPassword;

}
