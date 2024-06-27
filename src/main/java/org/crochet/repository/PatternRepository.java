package org.crochet.repository;

import org.crochet.model.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatternRepository extends JpaRepository<Pattern, String>, JpaSpecificationExecutor<Pattern> {
    @Query("""
            SELECT p
            FROM Pattern p
            LEFT JOIN FETCH p.files
            LEFT JOIN FETCH p.images
            WHERE p.isHome = true
            ORDER BY p.createdDate DESC
            LIMIT 12
            """)
    List<Pattern> findLimitedNumPatternByCreatedDateDesc();

    @Query("""
            select p
            from Pattern p
            join fetch p.category c
            where c.id = ?1
            """)
    List<Pattern> findPatternByCategory(String categoryId);

    @Query("""
            SELECT p
            FROM Pattern p
            LEFT JOIN FETCH p.files
            LEFT JOIN FETCH p.images
            WHERE p.id = ?1
            """)
    Optional<Pattern> getDetail(String id);
}