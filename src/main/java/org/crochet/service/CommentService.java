package org.crochet.service;

import org.crochet.payload.request.CommentRequest;
import org.crochet.payload.response.CommentResponse;

public interface CommentService {
    CommentResponse createOrUpdate(CommentRequest request);
}
