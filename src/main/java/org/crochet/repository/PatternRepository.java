package org.crochet.repository;

import org.crochet.model.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PatternRepository extends JpaRepository<Pattern, UUID>, JpaSpecificationExecutor<Pattern> {

}