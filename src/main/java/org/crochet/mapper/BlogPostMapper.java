package org.crochet.mapper;

import org.crochet.model.BlogPost;
import org.crochet.response.BlogPostResponse;
import org.mapstruct.Mapper;
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
                .orElse(null);
    }
}