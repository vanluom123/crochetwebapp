package org.crochet.repository;

import org.crochet.model.CategoryFreePattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CategoryFreePatternRepo extends JpaRepository<CategoryFreePattern, UUID> {
    @Query("select c from CategoryFreePattern c where c.parent.id = ?1")
    List<CategoryFreePattern> findSubCategoryFreePatternsByParent(UUID parentId);
}