package org.crochet.mapper;

import org.crochet.model.BlogPost;
import org.crochet.response.BlogPostResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class BlogPostMapperImpl implements BlogPostMapper {
  @Override
  public BlogPostResponse toResponse(BlogPost blogPost) {
    if (blogPost == null) {
      return null;
    }

    return BlogPostResponse.builder()
        .id(blogPost.getId())
        .title(blogPost.getTitle())
        .content(blogPost.getContent())
        .imageUrl(blogPost.getImageUrl())
        .build();
  }

  @Override
  public List<BlogPostResponse> toResponses(List<BlogPost> blogPosts) {
    if (ObjectUtils.isEmpty(blogPosts)) {
      return null;
    }

    return blogPosts.stream()
        .map(this::toResponse)
        .toList();
  }
}
