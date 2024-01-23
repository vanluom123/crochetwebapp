package org.crochet.service.contact;

import org.crochet.model.Category;
import org.crochet.payload.request.CategoryCreationRequest;
import org.crochet.payload.request.CategoryCreationWithParentRequest;
import org.crochet.payload.request.CategoryUpdateRequest;
import org.crochet.payload.response.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    CategoryResponse createWithParent(CategoryCreationWithParentRequest request);

    CategoryResponse createNotParent(CategoryCreationRequest request);

    CategoryResponse updateNotParent(CategoryUpdateRequest request);

    List<CategoryResponse> getParentCategories();

    List<CategoryResponse> getSubCategories(UUID parentId);

    CategoryResponse getById(UUID id);

    Category findById(UUID id);
}
