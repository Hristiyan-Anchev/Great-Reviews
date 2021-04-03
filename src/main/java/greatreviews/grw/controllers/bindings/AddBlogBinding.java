package greatreviews.grw.controllers.bindings;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddBlogBinding {

    @NotBlank(message = "Title must be present")
    String title;

    // this will be filled in once the multipart file is processed
    String imageURL;

    @NotBlank(message = "Content must be present")
    String content;

    MultipartFile file;

}
