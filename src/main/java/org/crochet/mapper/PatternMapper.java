package org.crochet.mapper;

import org.crochet.model.Pattern;
import org.crochet.payload.response.PatternResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {FileMapper.class, ImageMapper.class})
public interface PatternMapper {
    PatternMapper INSTANCE = Mappers.getMapper(PatternMapper.class);

    @Mapping(target = "id", source = "id", qualifiedByName = "uuidToString")
    PatternResponse toResponse(Pattern pattern);

    List<PatternResponse> toResponses(Collection<Pattern> patterns);

    @Named("uuidToString")
    default String uuidToString(UUID uuid) {
        return uuid.toString();
    }
}