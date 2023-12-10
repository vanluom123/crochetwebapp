package org.crochet.service.contact;

import org.crochet.request.PatternRequest;
import org.crochet.response.PatternPaginationResponse;
import org.crochet.response.PatternResponse;

import java.util.List;

public interface PatternService {
    void createOrUpdate(PatternRequest request);

    PatternPaginationResponse getPatterns(int pageNo, int pageSize, String sortBy, String sortDir, String text);

    List<PatternResponse> getLimitedPatterns();

    PatternResponse getDetail(String id);
}
