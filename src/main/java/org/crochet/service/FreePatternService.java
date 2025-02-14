package org.crochet.service;

import org.crochet.payload.request.Filter;
import org.crochet.payload.request.FreePatternRequest;
import org.crochet.payload.request.PaginationRequest;
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.payload.response.PaginationResponse;

import java.util.List;

public interface FreePatternService {
    void createOrUpdate(FreePatternRequest request);

    PaginationResponse<FreePatternResponse> getAllFreePatterns(int pageNo, int pageSize, String sortBy, String sortDir, Filter[] filters);

    PaginationResponse<FreePatternResponse> getAllByUser(int pageNo, int pageSize, String sortBy, String sortDir, Filter[] filters, String userId);

    List<FreePatternResponse> getLimitedFreePatterns();

    FreePatternResponse getDetail(String id);

    void delete(String id);

    List<String> getFreePatternIds(int pageNo, int limit);

    void deleteAllById(List<String> ids);

    List<FreePatternResponse> getFrepsByCreateBy(String userId);

    PaginationResponse<FreePatternResponse> getFrepsByCollectionId(String collectionId,
                                                                   PaginationRequest paginationRequest);
}
