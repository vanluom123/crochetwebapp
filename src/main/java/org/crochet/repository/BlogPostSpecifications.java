package org.crochet.repository;

import jakarta.persistence.criteria.JoinType;
import org.crochet.model.BlogPost;
import org.springframework.data.jpa.domain.Specification;

public class BlogPostSpecifications {

    public static Specification<BlogPost> fetchJoin() {
        return (r, q, cb) -> {
            if (Long.class != q.getResultType()) {
                r.fetch("files", JoinType.LEFT);
            }
            q.distinct(true);
            return cb.conjunction();
        };
    }
}
