package org.crochet.service;

import org.crochet.payload.request.FreePatternRequest;
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.payload.response.PaginatedFreePatternResponse;

import java.util.List;
import java.util.UUID;

public interface FreePatternService {
    FreePatternResponse createOrUpdate(FreePatternRequest request);

    PaginatedFreePatternResponse getFreePatterns(int pageNo, int pageSize, String sortBy, String sortDir, String text);

    List<FreePatternResponse> getLimitedFreePatterns();

    FreePatternResponse getDetail(String id);

    void delete(UUID id);

    List<FreePatternResponse> filterByCategory(UUID categoryId);
}
