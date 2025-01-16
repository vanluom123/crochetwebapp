package org.crochet.repository;

import org.crochet.model.Pattern;
import org.crochet.payload.response.PatternOnHome;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatternRepository extends JpaRepository<Pattern, String>, JpaSpecificationExecutor<Pattern> {

    @Query("""
            SELECT new org.crochet.payload.response.PatternOnHome(p.id, p.name, p.description, p.price, p.currencyCode, i.fileContent)
            FROM Pattern p
            LEFT JOIN p.images i
            WHERE p.isHome = true
                AND i.order = 0
            """)
    List<PatternOnHome> findLimitedNumPattern(Pageable pageable);

    @Query("""
            select new org.crochet.payload.response.PatternOnHome(p.id, p.name, p.description, p.price, p.currencyCode, i.fileContent)
            from Pattern p
            left join p.images i
            where p.id in :patternIds
                and i.order = 0
            """)
    Page<PatternOnHome> findPatternOnHomeWithIds(@Param("patternIds") List<String> ids, Pageable pageable);

    @Query("""
            select p.id
            from Pattern p
            order by p.createdDate desc
            """)
    List<String> getPatternIds(Pageable pageable);
}