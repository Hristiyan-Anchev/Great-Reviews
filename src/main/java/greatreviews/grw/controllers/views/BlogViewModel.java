package greatreviews.grw.controllers.views;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogViewModel {

    Long id;
    String title;
    String content;
    String created;
    String imageURL;

}
