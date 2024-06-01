package org.crochet.repository;

import org.crochet.model.BlogCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogCategoryRepo extends JpaRepository<BlogCategory, String> {
}