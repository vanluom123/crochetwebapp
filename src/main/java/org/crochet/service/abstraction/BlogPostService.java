package org.crochet.service.abstraction;

import org.crochet.request.BlogPostRequest;
import org.crochet.response.BlogPostPaginationResponse;
import org.crochet.response.BlogPostResponse;

public interface BlogPostService {
  void createOrUpdatePost(BlogPostRequest request);

  BlogPostPaginationResponse getBlogs(int pageNo, int pageSize, String sortBy, String sortDir, String text);

  BlogPostResponse getDetail(long id);
}
