package org.crochet.repository;

import org.crochet.model.PatternFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PatternFileRepository extends JpaRepository<PatternFile, UUID> {
}