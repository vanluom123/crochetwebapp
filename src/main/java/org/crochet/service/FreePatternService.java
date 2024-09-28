package org.crochet.service;

import org.crochet.payload.request.Filter;
import org.crochet.payload.request.FreePatternRequest;
import org.crochet.payload.response.FreeChartDetailResponse;
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.payload.response.PaginatedFreePatternResponse;
import org.crochet.security.UserPrincipal;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface FreePatternService {
    FreePatternResponse createOrUpdate(FreePatternRequest request);

    PaginatedFreePatternResponse getAllFreePatterns(int pageNo, int pageSize, String sortBy, Sort.Direction sortDir, Filter[] filters, UserPrincipal principal);

    List<FreePatternResponse> getLimitedFreePatterns();

    FreeChartDetailResponse getDetail(String id);

    void delete(String id);

    PaginatedFreePatternResponse getAllSavedPatternByUser(int pageNo, int pageSize, String sortBy, Sort.Direction sortDir, Filter[] filters,
                                                          UserPrincipal principal);
}
