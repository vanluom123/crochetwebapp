package org.crochet.repository;

import org.crochet.model.Pattern;
import org.crochet.payload.response.PatternOnHome;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatternRepository extends JpaRepository<Pattern, String>, JpaSpecificationExecutor<Pattern> {

    @Query("""
            SELECT new org.crochet.payload.response.PatternOnHome(p.id, p.name, p.description, p.price, p.currencyCode, i.fileContent)
            FROM Pattern p
            JOIN p.images i
            WHERE p.isHome = true
                AND i.order = 0
            """)
    List<PatternOnHome> findLimitedNumPattern(Pageable pageable);

    @Query("""
            SELECT p
            FROM Pattern p
            LEFT JOIN FETCH p.files
            LEFT JOIN FETCH p.images
            WHERE p.id = ?1
            """)
    Optional<Pattern> getDetail(String id);

    @Query("""
            select new org.crochet.payload.response.PatternOnHome(p.id, p.name, p.description, p.price, p.currencyCode, i.fileContent)
            from Pattern p
            join p.images i
            where p.id in :patternIds
                and i.order = 0
            """)
    List<PatternOnHome> findPatternOnHomeWithIds(@Param("patternIds") List<String> ids);
}