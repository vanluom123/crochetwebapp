package org.crochet.service;

import org.crochet.payload.request.Filter;
import org.crochet.payload.request.FreePatternRequest;
import org.crochet.payload.response.FreePatternOnHome;
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.payload.response.PaginatedFreePatternResponse;
import org.crochet.security.UserPrincipal;

import java.util.List;

public interface FreePatternService {
    FreePatternResponse createOrUpdate(FreePatternRequest request);

    PaginatedFreePatternResponse getAllFreePatterns(int pageNo, int pageSize, String sortBy, String sortDir, Filter[] filters);

    PaginatedFreePatternResponse getAllFreePatternsOnAdminPage(UserPrincipal currentUser,
                                                               int pageNo,
                                                               int pageSize,
                                                               String sortBy,
                                                               String sortDir,
                                                               Filter[] filters);

    List<FreePatternOnHome> getLimitedFreePatterns();

    FreePatternResponse getDetail(String id);

    void delete(UserPrincipal currentUser, String id);

    List<String> getFreePatternIds(int pageNo, int limit);
}
