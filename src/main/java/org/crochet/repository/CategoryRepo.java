package org.crochet.repository;

import org.crochet.model.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepo extends JpaRepository<Category, String> {

    @Query("""
            select (count(c.id) > 0)
            from Category c
            where c.name = ?1 and c.parent is null
            """)
    boolean existsByNameAndParentIsNull(String name);

    @Query("""
            select (count(c.id) > 0)
            from Category c
            where c.name = ?1 and c.parent is not null
            """)
    boolean existsByNameAndParentIsNotNull(String name);

    @EntityGraph(attributePaths = {"children"})
    @Query("select c from Category c")
    List<Category> getCategories();

    @EntityGraph(attributePaths = {"children"})
    @Query("""
            select c
            from Category c
            where c.id in :ids
            """)
    List<Category> findCategoriesByIds(@Param("ids") String... ids);

    @EntityGraph(attributePaths = {"children"})
    @Query("""
            select c
            from Category c
            where c.id = :id
            """)
    Optional<Category> findCategoryById(@Param("id") String id);
}