package greatreviews.grw.entities;

import greatreviews.grw.entities.basic.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewEntity extends BaseEntity {

    @Column
    String title;

    @Column
    String content;

    @Column
    String vote;

    @Column
    Boolean isPublished;

    @Column
    Boolean isCensored;



    @ManyToOne(fetch = FetchType.EAGER)
            @JoinColumn(name = "user_id",referencedColumnName = "id")
    UserEntity user;

    @ManyToOne(fetch = FetchType.EAGER)
            @JoinColumn(name = "company_id", referencedColumnName = "id")
    CompanyEntity company;

    @ManyToMany(mappedBy = "flaggedReviews")
    Set<UserEntity> usersFlagged;

}
