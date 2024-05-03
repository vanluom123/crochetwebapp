package org.crochet.repository;

import org.crochet.model.BlogPost;
import org.springframework.data.jpa.domain.Specification;

public class BlogPostSpecifications {
    public static Specification<BlogPost> searchBy(String text) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), "%" + text + "%");
    }
}
