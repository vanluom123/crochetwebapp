package org.crochet.service;

import org.crochet.payload.request.FreePatternRequest;
import org.crochet.payload.response.FreeChartDetailResponse;
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.payload.response.PaginatedFreePatternResponse;
import org.crochet.repository.Filter;

import java.util.List;

public interface FreePatternService {
    FreePatternResponse createOrUpdate(FreePatternRequest request);

    PaginatedFreePatternResponse getFreePatterns(int pageNo, int pageSize, String sortBy, String sortDir,
                                                 String searchText, String categoryId, List<Filter> filters);

    List<FreePatternResponse> getLimitedFreePatterns();

    FreeChartDetailResponse getDetail(String id);

    void delete(String id);
}
