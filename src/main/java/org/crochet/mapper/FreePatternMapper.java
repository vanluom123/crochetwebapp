package org.crochet.mapper;

import org.crochet.model.FreePattern;
import org.crochet.payload.request.FreePatternRequest;
import org.crochet.payload.response.FreeChartDetailResponse;
import org.crochet.payload.response.FreePatternResponse;
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
public interface FreePatternMapper extends PartialUpdate<FreePattern, FreePatternRequest> {
    FreePatternMapper INSTANCE = Mappers.getMapper(FreePatternMapper.class);

    @Mapping(target = "isHome", source = "home")
    FreePatternResponse toResponse(FreePattern pattern);

    @Mapping(target = "home", source = "home")
    FreeChartDetailResponse toFreeChartDetailResponse(FreePattern pattern);

    List<FreePatternResponse> toResponses(Collection<FreePattern> freePatterns);
}