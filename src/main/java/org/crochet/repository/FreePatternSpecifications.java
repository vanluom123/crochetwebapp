package org.crochet.repository;

import org.crochet.model.FreePattern;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class FreePatternSpecifications {
    public static Specification<FreePattern> searchByNameDescOrAuthor(String searchText) {
        return (r, q, cb) -> {
            var searchTextLowerCase = searchText.toLowerCase();
            var nameLike = cb.like(cb.lower(r.get("name")), "%" + searchTextLowerCase + "%");
            var descriptionLike = cb.like(cb.lower(r.get("description")), "%" + searchTextLowerCase + "%");
            var authorLike = cb.like(cb.lower(r.get("author")), "%" + searchTextLowerCase + "%");
            return cb.or(nameLike, descriptionLike, authorLike);
        };
    }

    public static Specification<FreePattern> existIn(List<FreePattern> patterns) {
        return (r, q, cb) -> r.in(patterns);
    }
}
