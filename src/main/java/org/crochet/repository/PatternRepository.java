package org.crochet.repository;

import org.crochet.model.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PatternRepository extends JpaRepository<Pattern, String>, JpaSpecificationExecutor<Pattern> {
}