package org.crochet.service;

import org.crochet.model.Category;
import org.crochet.payload.request.CategoryCreationRequest;
import org.crochet.payload.request.CategoryUpdateRequest;
import org.crochet.payload.response.CategoryResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    @Transactional
    List<CategoryResponse> create(CategoryCreationRequest request);

    CategoryResponse update(CategoryUpdateRequest request);

    List<CategoryResponse> getParentCategories();

    List<CategoryResponse> getSubCategories(UUID parentId);

    List<CategoryResponse> getAllCategories();

    CategoryResponse getById(UUID id);

    Category findById(UUID id);

    void delete(UUID id);
}
