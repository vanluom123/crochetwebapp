package org.crochet.service;

import org.crochet.model.Pattern;
import org.crochet.payload.request.PatternRequest;
import org.crochet.payload.response.PaginationResponse;
import org.crochet.payload.response.PatternResponse;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface PatternService {
    void createOrUpdate(PatternRequest request);

    PaginationResponse<PatternResponse> getPatterns(int offset, int limit, String sortBy, String sortDir, String categoryId, Specification<Pattern> spec);

    List<PatternResponse> getLimitedPatterns();

    List<String> getPatternIds(int offset, int limit);

    PatternResponse getDetail(String id);

    Pattern findById(String id);

    void deletePattern(String id);
}
