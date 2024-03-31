package org.crochet.repository;

import org.crochet.model.FreePattern;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class FreePatternSpecifications {
    public static Specification<FreePattern> existIn(List<FreePattern> patterns) {
        return (r, q, cb) -> r.in(patterns);
    }
}
