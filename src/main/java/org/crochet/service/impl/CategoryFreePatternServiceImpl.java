package org.crochet.service.impl;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.CategoryFreePatternMapper;
import org.crochet.model.CategoryFreePattern;
import org.crochet.payload.request.CategoryCreationRequest;
import org.crochet.payload.request.CategoryUpdateRequest;
import org.crochet.payload.response.CategoryResponse;
import org.crochet.repository.CategoryFreePatternRepo;
import org.crochet.service.CategoryFreePatternService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryFreePatternServiceImpl implements CategoryFreePatternService {
    private final CategoryFreePatternRepo categoryFreePatternRepo;

    public CategoryFreePatternServiceImpl(CategoryFreePatternRepo categoryFreePatternRepo) {
        this.categoryFreePatternRepo = categoryFreePatternRepo;
    }

    @Transactional
    @Override
    public CategoryResponse create(CategoryCreationRequest request) {
        CategoryFreePattern parent = (request.getParentId() == null) ? null : findById(request.getParentId());
        var child = new CategoryFreePattern();
        child.setName(request.getName());
        child.setParent(parent);
        child = categoryFreePatternRepo.save(child);
        return CategoryFreePatternMapper.INSTANCE.toResponse(child);
    }

    @Transactional
    @Override
    public CategoryResponse update(CategoryUpdateRequest request) {
        var category = findById(request.getId());
        category.setName(request.getName());
        category = categoryFreePatternRepo.save(category);
        return CategoryFreePatternMapper.INSTANCE.toResponse(category);
    }

    @Override
    public List<CategoryResponse> getParentCategories() {
        var categories = categoryFreePatternRepo.findAll()
                .parallelStream()
                .filter(category -> category.getParent() == null)
                .toList();
        return CategoryFreePatternMapper.INSTANCE.toResponses(categories);
    }

    @Override
    public List<CategoryResponse> getSubCategories(UUID parentId) {
        var category = findById(parentId);
        var subcategories = category.getChildren();
        return CategoryFreePatternMapper.INSTANCE.toResponses(subcategories);
    }

    @Override
    public CategoryResponse getById(UUID id) {
        var category = findById(id);
        return CategoryFreePatternMapper.INSTANCE.toResponse(category);
    }

    @Override
    public CategoryFreePattern findById(UUID id) {
        return categoryFreePatternRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        var category = findById(id);
        categoryFreePatternRepo.delete(category);
    }
}
