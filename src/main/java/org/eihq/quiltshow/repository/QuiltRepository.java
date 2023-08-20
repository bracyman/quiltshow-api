package org.eihq.quiltshow.repository;

import java.util.List;
import java.util.Optional;

import org.eihq.quiltshow.model.Category;
import org.eihq.quiltshow.model.Quilt;
import org.eihq.quiltshow.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuiltRepository extends JpaRepository<Quilt, Long>, JpaSpecificationExecutor<Quilt> {

	List<Quilt> findByCategory(Category category);

	@Query("from Quilt q left join fetch q.tags t where t = ?1")
	List<Quilt> findQuiltsWithTag(Tag tag);

	Optional<Quilt> findByNumber(Integer number);
	
	
    @Modifying
    @Query("DELETE FROM Quilt q WHERE q.id = :id")
    void deleteById(@Param("id") Long id);
    
    @Query(value = "SELECT max(number) FROM Quilt")
    Integer getMaxQuiltNumber();
}
