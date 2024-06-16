package org.crochet.repository;

import org.crochet.model.FreePattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FreePatternRepository extends JpaRepository<FreePattern, String>, JpaSpecificationExecutor<FreePattern> {
    @Query("""
                    select f from FreePattern f
                    where f.isHome = true
                    order by f.createdDate desc
                    limit ?1
            """)
    List<FreePattern> findLimitedNumFreePatternByCreatedDateDesc(int limited);
}