package org.crochet.repository;

import org.crochet.model.Pattern;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class PatternSpecifications {
    public static Specification<Pattern> searchByNameOrDesc(String searchText) {
        return (r, q, cb) ->
                cb.like(cb.lower(r.get("name")), "%" + searchText.toLowerCase() + "%");
    }

    public static Specification<Pattern> in(List<Pattern> patterns) {
        return (root, query, criteriaBuilder) -> root.in(patterns);
    }
}
