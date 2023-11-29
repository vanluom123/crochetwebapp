package org.crochet.service;

import org.crochet.request.CommentRequest;

public interface CommentService {
    void createOrUpdate(CommentRequest request);
}
