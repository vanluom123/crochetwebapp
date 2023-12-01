package org.crochet.mapper;

import org.crochet.model.PatternFile;
import org.crochet.response.PatternFileResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {PatternMapper.class})
public interface PatternFileMapper {
    PatternFileMapper INSTANCE = Mappers.getMapper(PatternFileMapper.class);

    PatternFile toEntity(PatternFileResponse patternFileResponse);

    PatternFileResponse toResponse(PatternFile patternFile);

    default List<PatternFileResponse> toResponses(Collection<PatternFile> patternFiles) {
        if (ObjectUtils.isEmpty(patternFiles)) {
            return null;
        }
        return patternFiles.stream().map(this::toResponse)
                .toList();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PatternFile partialUpdate(PatternFileResponse patternFileResponse, @MappingTarget PatternFile patternFile);
}