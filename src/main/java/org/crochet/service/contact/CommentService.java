package org.crochet.service.contact;

import org.crochet.payload.request.CommentRequest;

public interface CommentService {
    void createOrUpdate(CommentRequest request);
}
