package greatreviews.grw.services.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewServiceModel {


    String created;
    String userName;
    Long userId;
    Integer userReviewsCount;
    String userImageURL;

    String vote;
    String title;
    String content;

    String companyName;
    String companyLogo;
    Long companyId;
    String companyWebsite;


}
