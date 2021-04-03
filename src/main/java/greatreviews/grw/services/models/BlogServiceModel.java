package greatreviews.grw.services.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogServiceModel {

    Long id;

    String title;

    String imageURL;

    String content;

    Long authorId;
}
