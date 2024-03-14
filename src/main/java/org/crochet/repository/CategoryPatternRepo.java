package org.crochet.repository;

import org.crochet.model.CategoryPattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CategoryPatternRepo extends JpaRepository<CategoryPattern, UUID> {
    @Query("select c from CategoryPattern c where c.parent.id = ?1")
    List<CategoryPattern> findSubCategoryPatternsByParent(UUID parentId);
}