package greatreviews.grw.controllers.views;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrentUserViewModel {
    Long id;
    String username;
    String email;
}
