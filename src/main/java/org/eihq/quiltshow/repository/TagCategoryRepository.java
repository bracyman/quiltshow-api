package org.eihq.quiltshow.repository;

import org.eihq.quiltshow.model.TagCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagCategoryRepository extends JpaRepository<TagCategory, Long> {
    
}
