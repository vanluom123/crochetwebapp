package org.crochet.service;

import org.crochet.payload.request.BlogPostRequest;
import org.crochet.payload.response.BlogPostPaginationResponse;
import org.crochet.payload.response.BlogPostResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BlogPostService {
    BlogPostResponse createOrUpdatePost(BlogPostRequest request);

    BlogPostPaginationResponse getBlogs(int pageNo, int pageSize, String sortBy, String sortDir, String text);

    BlogPostResponse getDetail(String id);

    List<BlogPostResponse> getLimitedBlogPosts();

    @Transactional
    void deletePost(String id);
}
