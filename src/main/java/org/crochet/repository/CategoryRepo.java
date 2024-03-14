package org.crochet.repository;

import org.crochet.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepo extends JpaRepository<Category, UUID> {
    Optional<Category> findByName(String name);
    @Query("select c from Category c where c.parent.id = ?1")
    List<Category> findSubCategoriesByParent(UUID parentId);
}