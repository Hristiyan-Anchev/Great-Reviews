package greatreviews.grw.repositories;



import greatreviews.grw.entities.ReviewEntity;
import greatreviews.grw.services.models.ReviewServiceModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Set;



@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity,Long> {

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.company.id = ?1")
    Long getCompanyReviewsCount(Long companyId);

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.company.id = ?1 AND r.vote = '1'")
    Long getCompanyUpVotesCount(Long companyId);

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.company.id = ?1 AND r.vote = '-1'")
    Long getCompanyDownVotesCount(Long companyId);

    @Query("SELECT r FROM ReviewEntity r WHERE r.company.id = ?1 ORDER BY r.created DESC")
    Set<ReviewEntity> getCompanyReviewsById(Long companyId);

    @Query("SELECT r FROM ReviewEntity r ORDER BY r.created DESC")
    Page<ReviewEntity> getLatestReviews(Pageable pageable);
}
