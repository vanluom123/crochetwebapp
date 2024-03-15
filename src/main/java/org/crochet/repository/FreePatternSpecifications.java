package org.crochet.repository;

import org.crochet.model.FreePattern;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public class FreePatternSpecifications {
    public static Specification<FreePattern> searchBy(String text) {
        return (r, q, cb) -> {
            var name = cb.like(r.get("name"), "%" + text + "%");
            var desc = cb.like(r.get("description"), "%" + text + "%");
            return cb.or(name, desc);
        };
    }

    public static Specification<FreePattern> filterBy(List<UUID> categoryIds) {
        return (r, q, cb) -> r.get("category").get("id").in(categoryIds);
    }
}
