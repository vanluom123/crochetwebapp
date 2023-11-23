package org.crochet.mapper;

import org.crochet.model.FreePattern;
import org.crochet.model.Pattern;
import org.crochet.request.PatternRequest;
import org.crochet.response.PatternResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
public class PatternMapperImpl implements PatternMapper {
  @Override
  public PatternResponse toResponse(Pattern pattern) {
    if (pattern == null) {
      return null;
    }

    return PatternResponse.builder()
        .id(pattern.getId())
        .name(pattern.getName())
        .image(pattern.getImage())
        .description(pattern.getDescription())
        .price(pattern.getPrice())
        .build();
  }

  @Override
  public PatternResponse toResponse(FreePattern pattern) {
    if (pattern == null) {
      return null;
    }

    return PatternResponse.builder()
        .id(pattern.getId())
        .name(pattern.getName())
        .image(pattern.getImage())
        .description(pattern.getDescription())
        .build();
  }

  @Override
  public Pattern toPattern(PatternRequest request) {
    if (request == null) {
      return null;
    }

    return Pattern.builder()
        .id(request.getId())
        .name(request.getName())
        .image(request.getImage())
        .description(request.getDescription())
        .price(request.getPrice())
        .build();
  }

  @Override
  public FreePattern toFreePattern(PatternRequest request) {
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
  public List<PatternResponse> toResponses(List<Pattern> patterns) {
    if (ObjectUtils.isEmpty(patterns)) {
      return null;
    }

    return patterns.stream()
        .map(this::toResponse)
        .toList();
  }

  @Override
  public List<PatternResponse> toResponsesWithFreePattern(List<FreePattern> freePatterns) {
    if (ObjectUtils.isEmpty(freePatterns)) {
      return null;
    }

    return freePatterns.stream()
        .map(this::toResponse)
        .toList();
  }
}
