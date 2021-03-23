package greatreviews.grw.controllers.views;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyViewModel {
    
    Long id;
    String website;
    String email;
    String name;
    String address;
    String phone;
    String logo;
    String shortDescription;
    Boolean isVerified;
    Set<String> subcategories;
    Long upVotesCount;
    Long downVotesCount;
    Long reviewsCount;

}
