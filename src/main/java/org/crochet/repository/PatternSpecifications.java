package org.crochet.repository;

import jakarta.persistence.criteria.JoinType;
import org.crochet.model.Pattern;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class PatternSpecifications {

    public static Specification<Pattern> getAll() {
        return (r, q, cb) -> {
            if (Long.class != q.getResultType()) {
                r.fetch("files", JoinType.LEFT);
                r.fetch("images", JoinType.LEFT);
            }
            q.distinct(true);
            return cb.conjunction();
        };
    }

    public static Specification<Pattern> searchByNameOrDesc(String searchText) {
        return (r, q, cb) ->
                cb.like(cb.lower(r.get("name")), "%" + searchText.toLowerCase() + "%");
    }

    public static Specification<Pattern> in(List<Pattern> patterns) {
        return (root, query, criteriaBuilder) -> root.in(patterns);
    }
}
