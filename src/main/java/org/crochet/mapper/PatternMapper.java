package org.crochet.mapper;

import org.crochet.model.Pattern;
import org.crochet.payload.request.PatternRequest;
import org.crochet.payload.response.PatternResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {FileMapper.class, CategoryMapper.class}
)
public interface PatternMapper extends PartialUpdate<Pattern, PatternRequest> {
    PatternMapper INSTANCE = Mappers.getMapper(PatternMapper.class);

    @Mapping(target = "isHome", source = "home")
    PatternResponse toResponse(Pattern pattern);

    List<PatternResponse> toResponses(Collection<Pattern> patterns);
}