package org.crochet.mapper;

import org.crochet.model.FreePattern;
import org.crochet.model.FreePatternFile;
import org.crochet.request.FreePatternRequest;
import org.crochet.response.FreePatternResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Mapper
public interface FreePatternMapper {
    FreePatternMapper INSTANCE = Mappers.getMapper(FreePatternMapper.class);

    @Mapping(target = "id", source = "id", qualifiedByName = "uuidToString")
    @Mapping(target = "encodingBytes", source = "freePatternImages", qualifiedByName = "toList")
    FreePatternResponse toResponse(FreePattern pattern);

    List<FreePatternResponse> toResponses(Collection<FreePattern> freePatterns);

    @Named("toList")
    default List<String> toList(Collection<FreePatternFile> freePatternFiles) {
        return freePatternFiles.stream()
                .map(FreePatternFile::getBytes)
                .toList();
    }

    @Named("uuidToString")
    default String uuidToString(UUID uuid) {
        return uuid.toString();
    }
}