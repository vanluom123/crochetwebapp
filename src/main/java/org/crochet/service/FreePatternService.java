package org.crochet.service;

import org.crochet.payload.request.Filter;
import org.crochet.payload.request.FreePatternRequest;
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.payload.response.PaginatedFreePatternResponse;

import java.util.List;

public interface FreePatternService {
    void createOrUpdate(FreePatternRequest request);

    PaginatedFreePatternResponse getAllFreePatterns(int pageNo, int pageSize, String sortBy, String sortDir, Filter[] filters);

    PaginatedFreePatternResponse getAllByUser(int pageNo, int pageSize, String sortBy, String sortDir, Filter[] filters, String userId);

    List<FreePatternResponse> getLimitedFreePatterns();

    FreePatternResponse getDetail(String id);

    void delete(String id);

    List<String> getFreePatternIds(int pageNo, int limit);

    void deleteAllById(List<String> ids);
}
