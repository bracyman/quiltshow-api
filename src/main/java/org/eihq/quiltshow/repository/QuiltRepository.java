package org.eihq.quiltshow.repository;

import org.eihq.quiltshow.model.Quilt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuiltRepository extends JpaRepository<Quilt, Long> {

}
