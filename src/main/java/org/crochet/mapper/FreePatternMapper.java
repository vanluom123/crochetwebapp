package org.crochet.mapper;

import org.crochet.model.FreePattern;
import org.crochet.request.FreePatternRequest;
import org.crochet.response.FreePatternResponse;

import java.util.List;

public interface FreePatternMapper {
    FreePattern toFreePattern(FreePatternRequest request);

    FreePatternResponse toResponse(FreePattern pattern);

    List<FreePatternResponse> toResponses(List<FreePattern> freePatterns);
}
