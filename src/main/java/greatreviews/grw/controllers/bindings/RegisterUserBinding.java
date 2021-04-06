package greatreviews.grw.controllers.bindings;

import greatreviews.grw.enums.PatternEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class RegisterUserBinding {
    @Pattern(regexp = PatternEnum.Constants.EMAIL_PATTERN,message = "Email is in the wrong format")
    String email;

    @Pattern(regexp = PatternEnum.Constants.USERNAME_PATTERN,message = "Username is in the wrong format")
    String username;

    @PastOrPresent(message = "Date is invalid")
    @DateTimeFormat(pattern = PatternEnum.Constants.BIRTH_DATE_PATTERN)
    @NotNull(message = "Birth date must be specified")
    LocalDate birthDate;

    @Pattern(regexp = PatternEnum.Constants.PASSWORD_PATTERN,message = "Password is in the wrong format")
    String password;

    @Pattern(regexp = PatternEnum.Constants.PASSWORD_PATTERN,message = "Password is in the wrong format")
    String confirmPassword;

    String remoteHost;

}
