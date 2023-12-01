package org.crochet.mapper;

import org.crochet.model.FreePatternFile;
import org.crochet.response.FreePatternFileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {FreePatternMapper.class})
public interface FreePatternFileMapper {
    FreePatternFileMapper INSTANCE = Mappers.getMapper(FreePatternFileMapper.class);

    FreePatternFile toEntity(FreePatternFileResponse freePatternFileResponse);

    FreePatternFileResponse toResponse(FreePatternFile freePatternFile);

    default List<FreePatternFileResponse> toResponses(Collection<FreePatternFile> freePatternFiles) {
        if (ObjectUtils.isEmpty(freePatternFiles)) {
            return null;
        }
        return freePatternFiles.stream()
                .map(this::toResponse)
                .toList();
    }
}