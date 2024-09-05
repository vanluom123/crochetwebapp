package org.crochet.repository;

import jakarta.persistence.criteria.JoinType;
import org.crochet.model.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecifications {

    public static Specification<Product> fetchJoin() {
        return (r, q, cb) -> {
            if (Long.class != q.getResultType()) {
                r.fetch("images", JoinType.LEFT);
            }
            q.distinct(true);
            return cb.conjunction();
        };
    }
}
