package org.crochet.repository;

import org.crochet.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepo extends JpaRepository<Category, UUID> {
    @Query("select c from Category c where c.name = ?1")
    Optional<Category> findByName(String name);

    @Query("select (count(c.id) > 0) from Category c where c.name = ?1 and c.parent is null")
    boolean existsByNameAndParentIsNull(String name);

    @Query("select (count(c.id) > 0) from Category c where c.name = ?1 and c.parent is not null")
    boolean existsByNameAndParentIsNotNull(String name);
}