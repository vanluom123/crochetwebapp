package org.crochet.repository;

import org.crochet.model.FreePatternFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FreePatternFileRepository extends JpaRepository<FreePatternFile, UUID> {
}
