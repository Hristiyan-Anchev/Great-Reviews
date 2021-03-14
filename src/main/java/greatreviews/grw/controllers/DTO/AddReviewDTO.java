package greatreviews.grw.controllers.DTO;

import greatreviews.grw.enums.PatternEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddReviewDTO {

    @NotNull(message = "You must choose a rating")
    @Pattern(regexp = "^1|-1$",message = "Choose an appropriate rating")
    String vote;

    @Length(min = 10, max=100, message="Title must be between 10 and 100 characters")
    @NotBlank(message = "Please provide a title")
    String title;

    @Length(min=0,max=5000,message = "Review content is too long")
    String content;





}
