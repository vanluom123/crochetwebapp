package org.crochet.mapper;

import org.crochet.model.FreePattern;
import org.crochet.payload.request.FreePatternRequest;
import org.crochet.payload.response.FreePatternResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {FileMapper.class}
)
public interface FreePatternMapper extends PartialUpdate<FreePattern, FreePatternRequest> {
    FreePatternMapper INSTANCE = Mappers.getMapper(FreePatternMapper.class);

    @Mapping(target = "id", source = "id", qualifiedByName = "uuidToString")
    @Mapping(target = "isHome", source = "home")
    FreePatternResponse toResponse(FreePattern pattern);

    List<FreePatternResponse> toResponses(Collection<FreePattern> freePatterns);

    @Named("uuidToString")
    default String uuidToString(UUID uuid) {
        return uuid.toString();
    }
}