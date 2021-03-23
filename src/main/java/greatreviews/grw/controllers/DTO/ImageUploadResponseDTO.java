package greatreviews.grw.controllers.DTO;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImageUploadResponseDTO {

    String message;
    Boolean uploadSuccessful;
    String newLogoUrl;
}
