package org.crochet.service;

import org.crochet.payload.request.Filter;
import org.crochet.payload.request.PatternRequest;
import org.crochet.payload.response.PaginationResponse;
import org.crochet.payload.response.PatternResponse;

import java.util.List;

public interface PatternService {
    void createOrUpdate(PatternRequest request);

    PaginationResponse<PatternResponse> getPatterns(int offset, int limit, String sortBy, String sortDir, Filter[] filters);

    List<PatternResponse> getLimitedPatterns();

    List<String> getPatternIds(int offset, int limit);

    PatternResponse getDetail(String id);

    void deletePattern(String id);
}
