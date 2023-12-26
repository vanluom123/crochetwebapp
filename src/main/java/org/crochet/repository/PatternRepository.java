package org.crochet.repository;

import org.crochet.model.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatternRepository extends JpaRepository<Pattern, UUID>, JpaSpecificationExecutor<Pattern> {
    @Query("select p from Pattern p " +
            "join fetch p.orderPatternDetails opd " +
            "join fetch opd.order o " +
            "join fetch o.user u " +
            "where u.id = ?1 and p.id = ?2 and opd.status = 'COMPLETED'")
    Optional<Pattern> findPatternByUserOrdered(UUID userId, UUID patternId);
}