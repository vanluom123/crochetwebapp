package org.crochet.mapper;

import org.crochet.model.BlogFile;
import org.crochet.response.BlogFileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {BlogPostMapper.class})
public interface BlogFileMapper {
    BlogFileMapper INSTANCE = Mappers.getMapper(BlogFileMapper.class);

    BlogFileResponse toResponse(BlogFile blogFile);

    default List<BlogFileResponse> toResponses(Collection<BlogFile> blogFiles) {
        return Optional.ofNullable(blogFiles)
                .map(file -> file.stream()
                        .map(this::toResponse)
                        .toList())
                .orElse(null);
    }
}