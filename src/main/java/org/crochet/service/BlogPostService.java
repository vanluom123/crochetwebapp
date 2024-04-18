package org.crochet.service;

import org.crochet.payload.request.BlogPostRequest;
import org.crochet.payload.response.BlogPostPaginationResponse;
import org.crochet.payload.response.BlogPostResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface BlogPostService {
    BlogPostResponse createOrUpdatePost(BlogPostRequest request);

    BlogPostPaginationResponse getBlogs(int pageNo, int pageSize, String sortBy, String sortDir, String text);

    BlogPostResponse getDetail(UUID id);

    @Transactional
    void deletePost(UUID id);
}
