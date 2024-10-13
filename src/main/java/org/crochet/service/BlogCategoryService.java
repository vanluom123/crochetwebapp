package org.crochet.service;

import org.crochet.payload.request.BlogCategoryRequest;
import org.crochet.payload.response.BlogCategoryResponse;

import java.util.List;

public interface BlogCategoryService {

    BlogCategoryResponse getDetail(String id);

    List<BlogCategoryResponse> getAll();

    void delete(String id);

    BlogCategoryResponse createOrUpdate(BlogCategoryRequest request);
}
