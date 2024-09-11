package org.crochet.service;

import org.crochet.payload.request.BlogCategoryRequest;
import org.crochet.payload.response.BlogCategoryResponse;

import java.util.List;

public interface BlogCategoryService {
    BlogCategoryResponse create(BlogCategoryRequest request);

    BlogCategoryResponse update(BlogCategoryRequest request);

    BlogCategoryResponse getDetail(String id);

    List<BlogCategoryResponse> getAll();

    void delete(String id);
}
