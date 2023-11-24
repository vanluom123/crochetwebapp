package org.crochet.mapper;

import org.crochet.model.FreePattern;
import org.crochet.request.FreePatternRequest;
import org.crochet.response.FreePatternResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
public class FreePatternMapperImpl implements FreePatternMapper {

  @Override
  public FreePattern toFreePattern(FreePatternRequest request) {
    if (request == null) {
      return null;
    }

    return FreePattern.builder()
        .id(request.getId())
        .name(request.getName())
        .image(request.getImage())
        .description(request.getDescription())
        .build();
  }

  @Override
  public FreePatternResponse toResponse(FreePattern pattern) {
    if (pattern == null) {
      return null;
    }

    return FreePatternResponse.builder()
        .id(pattern.getId())
        .name(pattern.getName())
        .image(pattern.getImage())
        .description(pattern.getDescription())
        .build();
  }

  @Override
  public List<FreePatternResponse> toResponses(List<FreePattern> freePatterns) {
    if (ObjectUtils.isEmpty(freePatterns)) {
      return null;
    }

    return freePatterns.stream()
        .map(this::toResponse)
        .toList();
  }
}
