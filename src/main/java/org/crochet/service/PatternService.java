package org.crochet.service;

import org.crochet.payload.request.PatternRequest;
import org.crochet.payload.response.PatternPaginationResponse;
import org.crochet.payload.response.PatternResponse;
import org.crochet.security.UserPrincipal;

import java.util.List;
import java.util.UUID;

public interface PatternService {
    PatternResponse createOrUpdate(PatternRequest request);

    PatternPaginationResponse getPatterns(int pageNo, int pageSize, String sortBy, String sortDir, String text, List<UUID> categoryIds);

    List<PatternResponse> getLimitedPatterns();

    PatternResponse getDetail(UserPrincipal principal, String id);

    void deletePattern(UUID id);
}
