package greatreviews.grw.repositories;



import greatreviews.grw.entities.ReviewEntity;
import greatreviews.grw.services.models.ReviewServiceModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;



@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity,Long> {

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.company.id = ?1")
    Long getCompanyReviewsCount(Long companyId);

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.company.id = ?1 AND r.vote = '1'")
    Long getCompanyUpVotesCount(Long companyId);

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.company.id = ?1 AND r.vote = '-1'")
    Long getCompanyDownVotesCount(Long companyId);

    @Query("SELECT r FROM ReviewEntity r WHERE r.company.id = ?1 AND r.isPublished = ?2 ORDER BY r.created DESC")
    List<ReviewEntity> getCompanyReviewsById(Long companyId, Boolean published);

    @Query("SELECT r FROM ReviewEntity r WHERE r.isPublished = TRUE AND r.isCensored = FALSE AND r.isDeleted = FALSE ORDER BY r.created DESC")
    Page<ReviewEntity> getLatestReviews(Pageable pageable);

    @Query("SELECT r FROM ReviewEntity r WHERE r.user.id = ?1")
    Set<ReviewEntity> getReviewsByUser(Long userId);

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.isPublished = false")
    Integer countUnpublishedReviews();


    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.usersFlagged.size > 0")
    Integer countFlaggedReviews();

    @Query("SELECT r FROM ReviewEntity r where r.isPublished = false")
    List<ReviewEntity> getUnpublishedReviews();

    @Query("SELECT r FROM ReviewEntity r WHERE r.usersFlagged.size > 0 ")
    List<ReviewEntity> getFlaggedReviews();
}
