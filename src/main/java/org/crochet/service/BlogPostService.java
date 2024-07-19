package org.crochet.service;

import org.crochet.payload.request.BlogPostRequest;
import org.crochet.payload.request.Filter;
import org.crochet.payload.response.BlogPostPaginationResponse;
import org.crochet.payload.response.BlogPostResponse;

import java.util.List;

public interface BlogPostService {
    BlogPostResponse createOrUpdatePost(BlogPostRequest request);

    BlogPostPaginationResponse getBlogs(int pageNo, int pageSize, String sortBy, String sortDir, Filter[] filters);

    BlogPostResponse getDetail(String id);

    List<BlogPostResponse> getLimitedBlogPosts();

    void deletePost(String id);
}
