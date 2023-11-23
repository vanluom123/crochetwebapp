package org.crochet.mapper;

import org.crochet.model.FreePattern;
import org.crochet.model.Pattern;
import org.crochet.request.PatternRequest;
import org.crochet.response.PatternResponse;

import java.util.List;

public interface PatternMapper {
  PatternResponse toResponse(Pattern pattern);
  PatternResponse toResponse(FreePattern pattern);

  Pattern toPattern(PatternRequest request);
  FreePattern toFreePattern(PatternRequest request);

  List<PatternResponse> toResponses(List<Pattern> patterns);
  List<PatternResponse> toResponsesWithFreePattern(List<FreePattern> patterns);
}
