package org.crochet.repository;

import org.crochet.model.Category;
import org.springframework.stereotype.Repository;

@Repository
public class CustomCategoryRepo extends BaseRepositoryImpl<Category, String> {
}
