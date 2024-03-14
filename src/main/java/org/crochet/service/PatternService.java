package org.crochet.service;

import org.crochet.payload.request.PatternRequest;
import org.crochet.payload.response.PatternPaginationResponse;
import org.crochet.payload.response.PatternResponse;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface PatternService {
    PatternResponse createOrUpdate(PatternRequest request);

    PatternPaginationResponse getPatterns(int pageNo, int pageSize, String sortBy, String sortDir, String text);

    List<PatternResponse> getLimitedPatterns();

    PatternResponse getDetail(String id);

    void deletePattern(UUID id);

    Collection<PatternResponse> filterByCategory(UUID categoryId);
}
