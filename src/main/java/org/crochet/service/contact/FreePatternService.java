package org.crochet.service.contact;

import org.crochet.request.FreePatternRequest;
import org.crochet.response.FreePatternResponse;
import org.crochet.response.PaginatedFreePatternResponse;

import java.util.List;

public interface FreePatternService {
    void createOrUpdate(FreePatternRequest request);

    PaginatedFreePatternResponse getFreePatterns(int pageNo, int pageSize, String sortBy, String sortDir, String text);

    List<FreePatternResponse> getLimitedFreePatterns();

    FreePatternResponse getDetail(String id);
}
