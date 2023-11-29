package org.crochet.mapper;

import org.crochet.model.FreePattern;
import org.crochet.request.FreePatternRequest;
import org.crochet.response.FreePatternResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface FreePatternMapper {
    FreePatternMapper INSTANCE = Mappers.getMapper(FreePatternMapper.class);

    FreePattern toFreePattern(FreePatternRequest request);

    FreePatternResponse toResponse(FreePattern pattern);

    List<FreePatternResponse> toResponses(List<FreePattern> freePatterns);
}