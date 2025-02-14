package org.crochet.service;

import org.crochet.model.Category;
import org.crochet.payload.request.CategoryCreationRequest;
import org.crochet.payload.request.CategoryUpdateRequest;
import org.crochet.payload.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> create(CategoryCreationRequest request);

    CategoryResponse update(CategoryUpdateRequest request);

    List<CategoryResponse> getAllCategories();

    CategoryResponse getById(String id);

    Category findById(String id);

    void delete(String id);
}
