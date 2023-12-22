package org.crochet.mapper;

import org.crochet.model.Pattern;
import org.crochet.response.PatternResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

@Mapper
public interface PatternMapper {
    PatternMapper INSTANCE = Mappers.getMapper(PatternMapper.class);

    @Mapping(target = "id", source = "id", qualifiedByName = "uuidToString")
    PatternResponse toResponse(Pattern pattern);

    List<PatternResponse> toResponses(List<Pattern> patterns);

    @Named("uuidToString")
    default String uuidToString(UUID uuid) {
        return uuid.toString();
    }
}