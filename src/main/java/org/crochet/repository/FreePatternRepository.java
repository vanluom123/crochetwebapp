package org.crochet.repository;

import org.crochet.model.FreePattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FreePatternRepository extends JpaRepository<FreePattern, UUID>, JpaSpecificationExecutor<FreePattern> {
}