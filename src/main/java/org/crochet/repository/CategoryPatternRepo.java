package org.crochet.repository;

import org.crochet.model.CategoryPattern;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryPatternRepo extends JpaRepository<CategoryPattern, UUID> {
}