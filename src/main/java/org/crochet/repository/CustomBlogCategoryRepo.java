package org.crochet.repository;

import org.crochet.model.BlogCategory;
import org.springframework.stereotype.Repository;

@Repository
public class CustomBlogCategoryRepo extends BaseRepositoryImpl<BlogCategory, String> {
}
