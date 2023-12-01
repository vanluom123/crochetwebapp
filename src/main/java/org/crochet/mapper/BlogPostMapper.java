package org.crochet.mapper;

import org.crochet.model.BlogPost;
import org.crochet.response.BlogPostResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper
public interface BlogPostMapper {
    BlogPostMapper INSTANCE = Mappers.getMapper(BlogPostMapper.class);

    BlogPostResponse toResponse(BlogPost blogPost);

    List<BlogPostResponse> toResponses(Collection<BlogPost> blogPosts);

    BlogPost toEntity(BlogPostResponse blogPostResponse);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    BlogPost partialUpdate(BlogPostResponse blogPostResponse, @MappingTarget BlogPost blogPost);
}