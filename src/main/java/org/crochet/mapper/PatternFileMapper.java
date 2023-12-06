package org.crochet.mapper;

import org.crochet.model.PatternFile;
import org.crochet.response.PatternFileResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {PatternMapper.class})
public interface PatternFileMapper {
    PatternFileMapper INSTANCE = Mappers.getMapper(PatternFileMapper.class);

    PatternFile toEntity(PatternFileResponse patternFileResponse);

    PatternFileResponse toResponse(PatternFile patternFile);

    default List<PatternFileResponse> toResponses(Collection<PatternFile> patternFiles) {
        return Optional.ofNullable(patternFiles)
                .map(file -> file.stream()
                        .map(this::toResponse)
                        .toList())
                .orElseThrow(() -> new IllegalArgumentException("Input list cannot be null"));
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PatternFile partialUpdate(PatternFileResponse patternFileResponse, @MappingTarget PatternFile patternFile);
}