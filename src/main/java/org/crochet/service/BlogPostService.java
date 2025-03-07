package org.crochet.service;

import org.crochet.model.BlogPost;
import org.crochet.payload.request.BlogPostRequest;
import org.crochet.payload.response.BlogPostPaginationResponse;
import org.crochet.payload.response.BlogPostResponse;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface BlogPostService {
    void createOrUpdatePost(BlogPostRequest request);

    BlogPostResponse getDetail(String id);

    List<BlogPostResponse> getLimitedBlogPosts();

    BlogPostPaginationResponse getBlogs(int pageNo, int pageSize, String sortBy, String sortDir,
                                        Specification<BlogPost> spec);

    List<String> getBlogIds(int pageNo, int limit);

    void deletePost(String id);
}
