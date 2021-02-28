package greatreviews.grw.entities;

import greatreviews.grw.entities.basic.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogpostEntity extends BaseEntity {
    @Column
    String heading;

    @Column
    String imageURL;

    @Column(columnDefinition = "TEXT")
    String postContent;

//    Author author

}
