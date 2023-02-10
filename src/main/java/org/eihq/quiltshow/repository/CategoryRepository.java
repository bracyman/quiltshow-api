package org.eihq.quiltshow.repository;

import org.eihq.quiltshow.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    
}
