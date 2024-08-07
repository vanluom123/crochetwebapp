package org.crochet.repository;

import org.crochet.model.FreePattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FreePatternRepository extends JpaRepository<FreePattern, String>, JpaSpecificationExecutor<FreePattern> {
    @Query("""
            select f
            from FreePattern f
            left join fetch f.files
            left join fetch f.images
            where f.isHome = true
            order by f.createdDate desc
            limit 12
            """)
    List<FreePattern> findLimitedNumFreePatternByCreatedDateDesc();

    @Query("""
            select fp
            from FreePattern fp
            join fetch fp.category c
            where c.id = ?1
            """)
    List<FreePattern> findFreePatternByCategory(String categoryId);

    @Query("""
            select f
            from FreePattern f
            left join fetch f.files
            left join fetch f.images
            where f.id = ?1
            """)
    Optional<FreePattern> getDetail(String id);
}