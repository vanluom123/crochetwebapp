package org.crochet.repository;

import org.crochet.model.Pattern;
import org.springframework.data.domain.Pageable;
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
            WHERE p.isHome = true
            """)
    List<Pattern> findLimitedNumPattern(Pageable pageable);

    @Query("""
            SELECT p
            FROM Pattern p
            LEFT JOIN FETCH p.files
            LEFT JOIN FETCH p.images
            WHERE p.id = ?1
            """)
    Optional<Pattern> getDetail(String id);
}