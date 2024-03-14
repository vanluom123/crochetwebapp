package org.crochet.service.impl;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.CategoryPatternMapper;
import org.crochet.model.CategoryPattern;
import org.crochet.payload.request.CategoryCreationRequest;
import org.crochet.payload.request.CategoryUpdateRequest;
import org.crochet.payload.response.CategoryResponse;
import org.crochet.repository.CategoryPatternRepo;
import org.crochet.service.CategoryPatternService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryPatternServiceImpl implements CategoryPatternService {
    private final CategoryPatternRepo categoryPatternRepo;

    public CategoryPatternServiceImpl(CategoryPatternRepo categoryFreePatternRepo) {
        this.categoryPatternRepo = categoryFreePatternRepo;
    }

    @Transactional
    @Override
    public CategoryResponse create(CategoryCreationRequest request) {
        CategoryPattern parent = (request.getParentId() == null) ? null : findById(request.getParentId());
        var child = new CategoryPattern();
        child.setName(request.getName());
        child.setParent(parent);
        child = categoryPatternRepo.save(child);
        return CategoryPatternMapper.INSTANCE.toResponse(child);
    }

    @Transactional
    @Override
    public CategoryResponse update(CategoryUpdateRequest request) {
        var category = findById(request.getId());
        category.setName(request.getName());
        category = categoryPatternRepo.save(category);
        return CategoryPatternMapper.INSTANCE.toResponse(category);
    }

    @Override
    public List<CategoryResponse> getParentCategories() {
        var categories = categoryPatternRepo.findAll()
                .parallelStream()
                .filter(category -> category.getParent() == null)
                .toList();
        return CategoryPatternMapper.INSTANCE.toResponses(categories);
    }

    @Override
    public List<CategoryResponse> getSubCategories(UUID parentId) {
        var subcategories = categoryPatternRepo.findSubCategoryPatternsByParent(parentId);
        return CategoryPatternMapper.INSTANCE.toResponses(subcategories);
    }

    @Override
    public CategoryResponse getById(UUID id) {
        var category = findById(id);
        return CategoryPatternMapper.INSTANCE.toResponse(category);
    }

    @Override
    public CategoryPattern findById(UUID id) {
        return categoryPatternRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        var category = findById(id);
        categoryPatternRepo.delete(category);
    }
}
