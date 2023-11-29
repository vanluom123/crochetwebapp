package org.crochet.mapper;

import org.crochet.model.Pattern;
import org.crochet.request.PatternRequest;
import org.crochet.response.PatternResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PatternMapper {
    PatternMapper INSTANCE = Mappers.getMapper(PatternMapper.class);

    PatternResponse toResponse(Pattern pattern);

    @Mapping(target = "orderDetails", ignore = true)
    Pattern toPattern(PatternRequest request);

    List<PatternResponse> toResponses(List<Pattern> patterns);
}