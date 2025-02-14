package org.crochet.service;

import org.crochet.model.FreePattern;
import org.crochet.payload.request.FreePatternRequest;
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.payload.response.PaginationResponse;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface FreePatternService {
    void createOrUpdate(FreePatternRequest request);

    PaginationResponse<FreePatternResponse>
    getAllFreePatterns(int offset,
                       int limit,
                       String sortBy,
                       String sortDir,
                       String categoryId,
                       Specification<FreePattern> spec);

    PaginationResponse<FreePatternResponse>
    getAllByUser(int offset,
                 int limit,
                 String sortBy,
                 String sortDir,
                 String userId,
                 Specification<FreePattern> spec);

    List<FreePatternResponse> getLimitedFreePatterns();

    FreePatternResponse getDetail(String id);

    void delete(String id);

    List<String> getFreePatternIds(int offset, int limit);

    void deleteAllById(List<String> ids);

    PaginationResponse<FreePatternResponse> getFrepsByCollectionId(String collectionId, int offset, int limit, String sortBy, String sortDir);

    FreePattern findById(String id);
}
