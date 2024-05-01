package org.crochet.repository;

import org.crochet.model.FreePattern;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class FreePatternSpecifications {
    public static Specification<FreePattern> searchByNameDescOrAuthor(String searchText) {
        return (r, q, cb) -> {
            var nameLike = cb.like(r.get("name"), "%" + searchText + "%");
            var descriptionLike = cb.like(r.get("description"), "%" + searchText + "%");
            var authorLike = cb.like(r.get("author"), "%" + searchText + "%");
            return cb.or(nameLike, descriptionLike, authorLike);
        };
    }

    public static Specification<FreePattern> existIn(List<FreePattern> patterns) {
        return (r, q, cb) -> r.in(patterns);
    }
}
