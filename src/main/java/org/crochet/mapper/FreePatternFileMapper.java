package org.crochet.mapper;

import org.crochet.model.FreePatternFile;
import org.crochet.response.FreePatternFileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {FreePatternMapper.class})
public interface FreePatternFileMapper {
    FreePatternFileMapper INSTANCE = Mappers.getMapper(FreePatternFileMapper.class);

    FreePatternFile toEntity(FreePatternFileResponse freePatternFileResponse);

    FreePatternFileResponse toResponse(FreePatternFile freePatternFile);

    default List<FreePatternFileResponse> toResponses(Collection<FreePatternFile> freePatternFiles) {
        return Optional.ofNullable(freePatternFiles)
                .map(file -> file.stream()
                        .map(this::toResponse)
                        .toList())
                .orElseThrow(() -> new IllegalArgumentException("Input list cannot be null"));
    }
}