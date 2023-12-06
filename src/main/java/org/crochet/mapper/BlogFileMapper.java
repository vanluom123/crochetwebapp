package org.crochet.mapper;

import org.crochet.model.BlogFile;
import org.crochet.response.BlogFileResponse;
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
        uses = {BlogPostMapper.class})
public interface BlogFileMapper {
    BlogFileMapper INSTANCE = Mappers.getMapper(BlogFileMapper.class);

    BlogFile toEntity(BlogFileResponse blogFileResponse);

    BlogFileResponse toResponse(BlogFile blogFile);

    default List<BlogFileResponse> toResponses(Collection<BlogFile> blogFiles) {
        return Optional.ofNullable(blogFiles)
                .map(file -> file.stream()
                        .map(this::toResponse)
                        .toList())
                .orElseThrow(() -> new IllegalArgumentException("Input list cannot be null"));
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    BlogFile partialUpdate(BlogFileResponse blogFileResponse, @MappingTarget BlogFile blogFile);
}