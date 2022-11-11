package org.eihq.quiltshow.repository;

import org.eihq.quiltshow.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
    
}
