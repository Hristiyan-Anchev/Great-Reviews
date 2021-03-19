package greatreviews.grw.services.interfaces;

import greatreviews.grw.controllers.views.ReviewViewModel;
import greatreviews.grw.entities.ReviewEntity;
import greatreviews.grw.services.models.ReviewServiceModel;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface ReviewService {
    void addReview(ReviewServiceModel review, Long currentUserId,Long companyId);

    Long getCompanyReviewsCount(Long companyId);

    Long getCompanyUpVotesCount(Long companyId);

    Long getCompanyDownVotesCount(Long companyId);

    Set<ReviewServiceModel> getCompanyReviews(Long companyId);

    Set<ReviewServiceModel> getLatestReviews(int i);


    Set<ReviewServiceModel> getUserReviewsById(Long userId);
}
