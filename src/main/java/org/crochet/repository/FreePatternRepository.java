package org.crochet.repository;

import org.crochet.model.FreePattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface FreePatternRepository extends JpaRepository<FreePattern, UUID>, JpaSpecificationExecutor<FreePattern> {
    @Transactional
    @Modifying
    @Query("""
                update FreePattern f
                set f.isHome = ?2
                where f.id = ?1
            """)
    void updateHomeStatus(UUID freePatternId, boolean isHome);
}