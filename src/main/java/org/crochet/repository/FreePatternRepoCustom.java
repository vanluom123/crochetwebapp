package org.crochet.repository;

import org.crochet.model.FreePattern;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface FreePatternRepoCustom {
    List<String> findAllIds(Specification<FreePattern> spec);
}
