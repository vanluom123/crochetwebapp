package org.crochet.mapper;

import org.crochet.model.Pattern;
import org.crochet.model.PatternFile;
import org.crochet.response.PatternResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper
public interface PatternMapper {
    PatternMapper INSTANCE = Mappers.getMapper(PatternMapper.class);

    @Mapping(target = "id", source = "id", qualifiedByName = "uuidToString")
    @Mapping(target = "bytes", source = "patternFiles", qualifiedByName = "toList")
    PatternResponse toResponse(Pattern pattern);

    List<PatternResponse> toResponses(List<Pattern> patterns);

    @Named("toList")
    default List<String> toList(Collection<PatternFile> patternFiles) {
        return Optional.ofNullable(patternFiles)
                .map(file -> file.stream()
                        .map(PatternFile::getBytes)
                        .toList())
                .orElse(null);
    }

    @Named("uuidToString")
    default String uuidToString(UUID uuid) {
        return uuid.toString();
    }
}