package org.crochet.repository;

import jakarta.persistence.criteria.JoinType;
import org.crochet.model.Pattern;
import org.springframework.data.jpa.domain.Specification;

public class PatternSpecifications {

    public static Specification<Pattern> fetchJoin() {
        return (r, q, cb) -> {
            if (Long.class != q.getResultType()) {
                r.fetch("files", JoinType.LEFT);
                r.fetch("images", JoinType.LEFT);
            }
            q.distinct(true);
            return cb.conjunction();
        };
    }
}
