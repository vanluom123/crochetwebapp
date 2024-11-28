package org.crochet.service;

import org.crochet.payload.request.Filter;
import org.crochet.payload.request.PatternRequest;
import org.crochet.payload.response.PatternOnHome;
import org.crochet.payload.response.PatternPaginationResponse;
import org.crochet.payload.response.PatternResponse;

import java.util.List;

public interface PatternService {
    PatternResponse createOrUpdate(PatternRequest request);

    PatternPaginationResponse getPatterns(int pageNo, int pageSize, String sortBy, String sortDir, Filter[] filters);

    List<PatternOnHome> getLimitedPatterns();

    List<String> getPatternIds(int pageNo, int limit);

    PatternResponse getDetail(String id);

    void deletePattern(String id);
}
