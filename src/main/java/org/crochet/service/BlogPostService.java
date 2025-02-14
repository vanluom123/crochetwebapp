package org.crochet.service;

import org.crochet.payload.request.BlogPostRequest;
import org.crochet.payload.request.Filter;
import org.crochet.payload.response.BlogPostResponse;
import org.crochet.payload.response.PaginationResponse;

import java.util.List;

public interface BlogPostService {
    void createOrUpdatePost(BlogPostRequest request);

    PaginationResponse<BlogPostResponse> getBlogs(int pageNo, int pageSize, String sortBy, String sortDir, Filter[] filters);

    BlogPostResponse getDetail(String id);

    List<BlogPostResponse> getLimitedBlogPosts();

    List<String> getBlogIds(int pageNo, int limit);

    void deletePost(String id);
}
