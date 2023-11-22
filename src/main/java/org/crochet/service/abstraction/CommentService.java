package org.crochet.service.abstraction;

import org.crochet.request.CommentRequest;

public interface CommentService {
  void createOrUpdate(CommentRequest request);
}
