package greatreviews.grw.services.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClaimTokenServiceModel {

    Long id;
    Long companyId;
    String created;
    String value;
    Boolean isExpired;
    Boolean isVerified;



}
