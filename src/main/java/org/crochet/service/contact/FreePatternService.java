package org.crochet.service.contact;

import org.crochet.payload.request.FreePatternRequest;
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.payload.response.PaginatedFreePatternResponse;

import java.util.List;

public interface FreePatternService {
    FreePatternResponse createOrUpdate(FreePatternRequest request);

    PaginatedFreePatternResponse getFreePatterns(int pageNo, int pageSize, String sortBy, String sortDir, String text);

    List<FreePatternResponse> getLimitedFreePatterns();

    FreePatternResponse getDetail(String id);
}
