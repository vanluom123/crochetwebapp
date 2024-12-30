package org.crochet.repository;

import org.crochet.model.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Query("""
            select c
            from Category c
            join fetch c.children
            where c.id in :ids
            """)
    List<Category> findCategoriesByIds(@Param("ids") String... ids);
}