package org.crochet.mapper;

import org.crochet.model.BlogPost;
import org.crochet.response.BlogPostResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BlogPostMapper {
    BlogPostMapper INSTANCE = Mappers.getMapper(BlogPostMapper.class);

    BlogPostResponse toResponse(BlogPost blogPost);

    default List<BlogPostResponse> toResponses(Collection<BlogPost> blogPosts) {
        return Optional.ofNullable(blogPosts)
                .map(file -> file.stream()
                        .map(this::toResponse)
                        .toList())
                .orElseThrow(() -> new IllegalArgumentException("Input list cannot be null"));
    }

    BlogPost toEntity(BlogPostResponse blogPostResponse);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    BlogPost partialUpdate(BlogPostResponse blogPostResponse, @MappingTarget BlogPost blogPost);
}