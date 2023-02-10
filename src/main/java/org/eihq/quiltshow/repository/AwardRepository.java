package org.eihq.quiltshow.repository;

import org.eihq.quiltshow.model.Award;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AwardRepository extends JpaRepository<Award, Long> {
    
}
