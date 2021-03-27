package greatreviews.grw.controllers.views;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewViewModel {

    Long id;
    String created;
    String userName;
    Long userId;
    Integer userReviewsCount;
    String userImageURL;

    Boolean isCensored;
    Boolean isPublished;


    String vote;
    String title;
    String content;

    String companyName;
    String companyLogo;
    Long companyId;
    String companyWebsite;

    Set<Long> usersFlagged;
}
