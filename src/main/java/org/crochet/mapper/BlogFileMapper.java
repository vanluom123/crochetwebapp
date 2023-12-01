package org.crochet.mapper;

import org.crochet.model.BlogFile;
import org.crochet.response.BlogFileResponse;
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
        uses = {BlogPostMapper.class})
public interface BlogFileMapper {
    BlogFileMapper INSTANCE = Mappers.getMapper(BlogFileMapper.class);

    BlogFile toEntity(BlogFileResponse blogFileResponse);

    BlogFileResponse toResponse(BlogFile blogFile);

    default List<BlogFileResponse> toResponses(Collection<BlogFile> blogFiles) {
        if (ObjectUtils.isEmpty(blogFiles)) {
            return null;
        }
        return blogFiles.stream().map(this::toResponse)
                .toList();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    BlogFile partialUpdate(BlogFileResponse blogFileResponse, @MappingTarget BlogFile blogFile);
}