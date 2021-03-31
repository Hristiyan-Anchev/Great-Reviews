package greatreviews.grw.services.interfaces;

import greatreviews.grw.controllers.DTO.CensorResponseDTO;
import greatreviews.grw.controllers.DTO.FlagReviewResponseDTO;
import greatreviews.grw.controllers.DTO.PublishReviewResponseDTO;
import greatreviews.grw.controllers.views.ReviewViewModel;
import greatreviews.grw.entities.ReviewEntity;
import greatreviews.grw.services.models.ReviewServiceModel;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface ReviewService {
    void addReview(ReviewServiceModel review, Long currentUserId,Long companyId);

    Long getCompanyReviewsCount(Long companyId);

    Long getCompanyUpVotesCount(Long companyId);

    Long getCompanyDownVotesCount(Long companyId);

    List<ReviewServiceModel> getCompanyReviews(Long companyId, Boolean published);

    List<ReviewServiceModel> getLatestReviews(int i);

    List<ReviewServiceModel> getUserReviewsById(Long userId);

    FlagReviewResponseDTO addUserFlag(Long reviewId, Long id);

    Integer getUnpublishedReviewsCount();

    Integer getFlaggedReviewsCount();

    List<ReviewServiceModel> getUnpublishedReviews();

    PublishReviewResponseDTO publishReview(Long reviewId);

    List<ReviewServiceModel> getFlaggedReviews();

    CensorResponseDTO toggleReviewCensorById(Long reviewId);
}
