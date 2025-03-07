package org.crochet.service;

import org.crochet.model.FreePattern;
import org.crochet.payload.request.FreePatternRequest;
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.payload.response.PaginatedFreePatternResponse;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface FreePatternService {
    void createOrUpdate(FreePatternRequest request);

    PaginatedFreePatternResponse
    getAllFreePatterns(int pageNo,
                       int pageSize,
                       String sortBy,
                       String sortDir,
                       String categoryId,
                       Specification<FreePattern> spec);

    PaginatedFreePatternResponse
    getAllByUser(int pageNo,
                 int pageSize,
                 String sortBy,
                 String sortDir,
                 String userId,
                 Specification<FreePattern> spec);

    List<FreePatternResponse> getLimitedFreePatterns();

    FreePatternResponse getDetail(String id);

    void delete(String id);

    List<String> getFreePatternIds(int pageNo, int limit);

    void deleteAllById(List<String> ids);
}
