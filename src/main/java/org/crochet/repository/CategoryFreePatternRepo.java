package org.crochet.repository;

import org.crochet.model.CategoryFreePattern;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryFreePatternRepo extends JpaRepository<CategoryFreePattern, UUID> {
}