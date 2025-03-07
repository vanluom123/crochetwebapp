package org.crochet.repository;

import org.crochet.model.Pattern;
import org.crochet.payload.response.PatternResponse;
import org.springframework.data.domain.Page;
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
            SELECT
              p
            FROM
              Pattern p
              JOIN FETCH p.category
            WHERE
              p.id =:id
            """)
    Optional<Pattern> findPatternById(String id);

    @Query("""
            SELECT
              new org.crochet.payload.response.PatternResponse (
                p.id,
                p.name,
                p.description,
                p.price,
                p.currencyCode,
                i.fileContent
              )
            FROM
              Pattern p
              JOIN p.images i WITH i.order = 0
            WHERE
              p.isHome = TRUE
            """)
    List<PatternResponse> findLimitedNumPattern(Pageable pageable);

    @Query("""
            SELECT
              new org.crochet.payload.response.PatternResponse (
                p.id,
                p.name,
                p.description,
                p.price,
                p.currencyCode,
                i.fileContent
              )
            FROM
              Pattern p
              JOIN p.images i WITH i.order = 0
            WHERE
              p.id IN :patternIds
            """)
    Page<PatternResponse> findPatternWithIds(@Param("patternIds") List<String> ids, Pageable pageable);

    @Query("""
            SELECT
              new org.crochet.payload.response.PatternResponse (
                p.id,
                p.name,
                p.description,
                p.price,
                p.currencyCode,
                i.fileContent
              )
            FROM
              Pattern p
              JOIN p.images i WITH i.order = 0
            """)
    Page<PatternResponse> findPatternWithPageable(Pageable pageable);

    @Query("""
            SELECT
              p.id
            FROM
              Pattern p
            ORDER BY
              p.createdDate DESC
            """)
    List<String> getPatternIds(Pageable pageable);
}