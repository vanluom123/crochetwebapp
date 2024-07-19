package org.crochet.service;

import org.crochet.payload.request.Filter;
import org.crochet.payload.request.FreePatternRequest;
import org.crochet.payload.response.FreeChartDetailResponse;
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.payload.response.PaginatedFreePatternResponse;

import java.util.List;

public interface FreePatternService {
    FreePatternResponse createOrUpdate(FreePatternRequest request);

    PaginatedFreePatternResponse getAllFreePatterns(int pageNo, int pageSize, String sortBy, String sortDir, Filter[] filters);

    List<FreePatternResponse> getLimitedFreePatterns();

    FreeChartDetailResponse getDetail(String id);

    void delete(String id);
}
