package org.eihq.quiltshow.repository;

import org.eihq.quiltshow.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, String> {
    
}
