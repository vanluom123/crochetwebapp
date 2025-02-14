package org.crochet.service;

import org.crochet.model.BlogPost;
import org.crochet.payload.request.BlogPostRequest;
import org.crochet.payload.response.BlogPostResponse;
import org.crochet.payload.response.PaginationResponse;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface BlogPostService {
    void createOrUpdatePost(BlogPostRequest request);

    PaginationResponse<BlogPostResponse> getBlogs(int offset, int limit, String sortBy, String sortDir,
                                        Specification<BlogPost> spec);

    BlogPostResponse getDetail(String id);

    List<BlogPostResponse> getLimitedBlogPosts();

    List<String> getBlogIds(int offset, int limit);

    void deletePost(String id);

    BlogPost getById(String id);
}
