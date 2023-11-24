package org.crochet.mapper;

import org.crochet.model.BlogPost;
import org.crochet.response.BlogPostResponse;

import java.util.List;

public interface BlogPostMapper {
  BlogPostResponse toResponse(BlogPost blogPost);

  List<BlogPostResponse> toResponses(List<BlogPost> blogPosts);
}
