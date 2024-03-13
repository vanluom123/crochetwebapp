package org.crochet.service;

import org.crochet.model.CategoryPattern;
import org.crochet.payload.request.CategoryCreationRequest;
import org.crochet.payload.request.CategoryUpdateRequest;
import org.crochet.payload.response.CategoryResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface CategoryPatternService {
    @Transactional
    CategoryResponse create(CategoryCreationRequest request);

    @Transactional
    CategoryResponse update(CategoryUpdateRequest request);

    List<CategoryResponse> getParentCategories();

    List<CategoryResponse> getSubCategories(UUID parentId);

    CategoryResponse getById(UUID id);

    CategoryPattern findById(UUID id);

    @Transactional
    void delete(UUID id);
}
