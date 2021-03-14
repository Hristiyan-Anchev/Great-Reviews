package greatreviews.grw.controllers.views;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewViewModel {

//    Long companyId;
    String created;
    String userName;
    Integer userReviewsCount;
    String userImageURL;

    String vote;
    String title;
    String content;


}
