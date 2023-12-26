package org.crochet.service.contact;

import org.crochet.payload.request.FreePatternRequest;
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.payload.response.PaginatedFreePatternResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FreePatternService {
    String createOrUpdate(FreePatternRequest request, List<MultipartFile> files);

    PaginatedFreePatternResponse getFreePatterns(int pageNo, int pageSize, String sortBy, String sortDir, String text);

    List<FreePatternResponse> getLimitedFreePatterns();

    FreePatternResponse getDetail(String id);
}
