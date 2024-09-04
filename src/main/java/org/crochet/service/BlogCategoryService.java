package org.crochet.service;

import org.crochet.payload.request.BlogCategoryRequest;
import org.crochet.payload.response.BlogCategoryResponse;

import java.util.List;

public interface BlogCategoryService {
    BlogCategoryResponse createBlogCategory(BlogCategoryRequest request);

    BlogCategoryResponse updateBlogCategory(BlogCategoryRequest request);

    BlogCategoryResponse getBlogCategory(String id);

    List<BlogCategoryResponse> getBlogCategories();

    void deleteBlogCategory(String id);
}
