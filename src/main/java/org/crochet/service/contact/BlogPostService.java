package org.crochet.service.contact;

import org.crochet.payload.request.BlogPostRequest;
import org.crochet.payload.response.BlogPostPaginationResponse;
import org.crochet.payload.response.BlogPostResponse;

public interface BlogPostService {
    BlogPostResponse createOrUpdatePost(BlogPostRequest request);

    BlogPostPaginationResponse getBlogs(int pageNo, int pageSize, String sortBy, String sortDir, String text);

    BlogPostResponse getDetail(String id);
}
