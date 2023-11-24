package org.crochet.mapper;

import org.crochet.model.Pattern;
import org.crochet.request.PatternRequest;
import org.crochet.response.PatternResponse;

import java.util.List;

public interface PatternMapper {
  PatternResponse toResponse(Pattern pattern);

  Pattern toPattern(PatternRequest request);

  List<PatternResponse> toResponses(List<Pattern> patterns);
}
