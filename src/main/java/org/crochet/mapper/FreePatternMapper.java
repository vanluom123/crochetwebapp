package org.crochet.mapper;

import org.crochet.model.FreePattern;
import org.crochet.model.FreePatternFile;
import org.crochet.response.FreePatternResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper
public interface FreePatternMapper {
    FreePatternMapper INSTANCE = Mappers.getMapper(FreePatternMapper.class);

    @Mapping(target = "id", source = "id", qualifiedByName = "uuidToString")
    @Mapping(target = "encodingBytes", source = "freePatternFiles", qualifiedByName = "toList")
    FreePatternResponse toResponse(FreePattern pattern);

    List<FreePatternResponse> toResponses(Collection<FreePattern> freePatterns);

    @Named("toList")
    default List<String> toList(Collection<FreePatternFile> freePatternFiles) {
        return Optional.ofNullable(freePatternFiles)
                .map(files -> files.stream()
                        .map(FreePatternFile::getBytes)
                        .collect(Collectors.toList())
                )
                .orElseThrow(() -> new IllegalArgumentException("Input list cannot be null"));
    }

    @Named("uuidToString")
    default String uuidToString(UUID uuid) {
        return uuid.toString();
    }
}