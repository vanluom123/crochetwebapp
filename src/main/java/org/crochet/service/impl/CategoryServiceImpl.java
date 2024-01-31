package org.crochet.service.impl;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.CategoryMapper;
import org.crochet.model.Category;
import org.crochet.payload.request.CategoryCreationRequest;
import org.crochet.payload.request.CategoryUpdateRequest;
import org.crochet.payload.response.CategoryResponse;
import org.crochet.repository.CategoryRepo;
import org.crochet.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepo categoryRepo;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepo categoryRepo,
                               CategoryMapper categoryMapper) {
        this.categoryRepo = categoryRepo;
        this.categoryMapper = categoryMapper;
    }

    @Transactional
    @Override
    public CategoryResponse create(CategoryCreationRequest request) {
        Category parent = (request.getParentId() == null) ? null : findById(request.getParentId());
        var child = new Category();
        child.setName(request.getName());
        child.setParent(parent);
        child = categoryRepo.save(child);
        return categoryMapper.toResponse(child);
    }

    @Transactional
    @Override
    public CategoryResponse update(CategoryUpdateRequest request) {
        var category = findById(request.getId());
        category.setName(request.getName());
        category = categoryRepo.save(category);
        return categoryMapper.toResponse(category);
    }

    @Override
    public List<CategoryResponse> getParentCategories() {
        var categories = categoryRepo.findAll()
                .parallelStream()
                .filter(category -> category.getParent() == null)
                .toList();
        return categoryMapper.toResponses(categories);
    }

    @Override
    public List<CategoryResponse> getSubCategories(UUID parentId) {
        var category = findById(parentId);
        var subcategories = category.getChildren();
        return categoryMapper.toResponses(subcategories);
    }

    @Override
    public CategoryResponse getById(UUID id) {
        var category = findById(id);
        return categoryMapper.toResponse(category);
    }

    @Override
    public Category findById(UUID id) {
        return categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }
}
