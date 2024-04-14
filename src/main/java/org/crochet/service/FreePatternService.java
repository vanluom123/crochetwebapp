package org.crochet.service;

import org.crochet.payload.request.FreePatternRequest;
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.payload.response.PaginatedFreePatternResponse;
import org.crochet.repository.Filter;

import java.util.List;
import java.util.UUID;

public interface FreePatternService {
    FreePatternResponse createOrUpdate(FreePatternRequest request);

    PaginatedFreePatternResponse getFreePatterns(int pageNo, int pageSize, String sortBy, String sortDir,
                                                 String searchText, UUID categoryId, List<Filter> filters);

    List<FreePatternResponse> getLimitedFreePatterns();

    FreePatternResponse getDetail(UUID id);

    void delete(UUID id);

    void updateHomeStatus(UUID freePatternId, boolean isHome);
}
