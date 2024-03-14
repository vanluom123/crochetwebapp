package org.crochet.repository;

import org.crochet.model.FreePattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FreePatternRepository extends JpaRepository<FreePattern, UUID>, JpaSpecificationExecutor<FreePattern> {
    @Query("select fp from FreePattern fp where fp.categoryFreePattern.id = ?1")
    List<FreePattern> findFreePatternByCategoryFreePattern(UUID categoryId);
}