package org.crochet.service.abstraction;

import org.crochet.request.BlogPostRequest;

public interface BlogPostService {
  void createOrUpdatePost(BlogPostRequest request);
}
