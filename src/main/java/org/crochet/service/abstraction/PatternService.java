package org.crochet.service.abstraction;

import org.crochet.request.PatternRequest;
import org.crochet.response.PatternPaginationResponse;
import org.crochet.response.PatternResponse;

public interface PatternService {
  void createOrUpdate(PatternRequest request);

  PatternPaginationResponse getPatterns(int pageNo, int pageSize, String sortBy, String sortDir, String text);

  PatternResponse getDetail(long id);
}
