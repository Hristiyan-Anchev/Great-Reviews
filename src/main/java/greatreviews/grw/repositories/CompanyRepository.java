package greatreviews.grw.repositories;

import greatreviews.grw.entities.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    Optional<CompanyEntity> findCompanyEntityByWebsite(String website);
    Optional<CompanyEntity> findCompanyEntityByEmail(String email);
    Optional<CompanyEntity> findCompanyEntityByName(String name);
    Optional<CompanyEntity> findCompanyEntityById(Long id);

    Long countAllByCategoryId(Long categoryId);

    @Query("SELECT c FROM CompanyEntity c WHERE c.name LIKE CONCAT('%',?1,'%') OR c.website LIKE CONCAT('%',?1,'%')")
    Set<CompanyEntity> getCompaniesBySearchString(String searchString);

    Set<CompanyEntity> findCompanyEntitiesByOwnerId(Long ownerId);

//    @Query("SELECT company FROM CategoryEntity company JOIN ")
//    Set<CompanyEntity> findCompaniesBySubcategoryId(Long id);

}
