package org.crochet.service;

import org.crochet.payload.request.CommentRequest;
import org.crochet.payload.response.CommentResponse;
import org.crochet.security.UserPrincipal;

public interface CommentService {
    CommentResponse createOrUpdate(UserPrincipal principal, CommentRequest request);
}
