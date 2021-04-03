package greatreviews.grw.repositories;

import greatreviews.grw.entities.BlogpostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<BlogpostEntity,Long> {
    @Query("SELECT b FROM BlogpostEntity b")
    Page<BlogpostEntity> getLatestBlogs(Pageable pageable);
}
