package org.crochet.repository;

import org.crochet.model.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatternRepository extends JpaRepository<Pattern, String>, JpaSpecificationExecutor<Pattern> {
    @Query("""
            SELECT p
            FROM Pattern p
            WHERE p.isHome = true
            ORDER BY p.createdDate DESC
            LIMIT ?1
            """)
    List<Pattern> findLimitedNumPatternByCreatedDateDesc(int limited);
}