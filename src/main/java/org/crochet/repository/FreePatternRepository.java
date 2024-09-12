package org.crochet.repository;

import org.crochet.model.FreePattern;
import org.springframework.data.domain.Pageable;
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
            where f.isHome = true
            """)
    List<FreePattern> findLimitedNumFreePattern(Pageable pageable);

    @Query("""
            select f
            from FreePattern f
            left join fetch f.files
            left join fetch f.images
            where f.id = ?1
            """)
    Optional<FreePattern> getDetail(String id);

}