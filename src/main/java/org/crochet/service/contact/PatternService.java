package org.crochet.service.contact;

import org.crochet.request.PatternRequest;
import org.crochet.response.PatternPaginationResponse;
import org.crochet.response.PatternResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PatternService {
    String createOrUpdate(PatternRequest request, List<MultipartFile> files);

    PatternPaginationResponse getPatterns(int pageNo, int pageSize, String sortBy, String sortDir, String text);

    List<PatternResponse> getLimitedPatterns();

    PatternResponse getDetail(String id);
}
