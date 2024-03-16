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

import static org.crochet.constant.MessageCode.CATEGORY_NOT_FOUND_CODE;
import static org.crochet.constant.MessageConstant.CATEGORY_NOT_FOUND_MESSAGE;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepo categoryRepo;

    public CategoryServiceImpl(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @Transactional
    @Override
    public CategoryResponse create(CategoryCreationRequest request) {
        Category parent = (request.getParentId() == null) ? null : findById(request.getParentId());
        var child = new Category();
        child.setName(request.getName());
        child.setParent(parent);
        child = categoryRepo.save(child);
        return CategoryMapper.INSTANCE.toResponse(child);
    }

    @Transactional
    @Override
    public CategoryResponse update(CategoryUpdateRequest request) {
        var category = findById(request.getId());
        category.setName(request.getName());
        category = categoryRepo.save(category);
        return CategoryMapper.INSTANCE.toResponse(category);
    }

    @Override
    public List<CategoryResponse> getParentCategories() {
        var categories = categoryRepo.findAll()
                .parallelStream()
                .filter(category -> category.getParent() == null)
                .toList();
        return CategoryMapper.INSTANCE.toResponses(categories);
    }

    @Override
    public List<CategoryResponse> getSubCategories(UUID parentId) {
        var parent = findById(parentId);
        var subcategories = parent.getChildren();
        return CategoryMapper.INSTANCE.toResponses(subcategories);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        var categories = categoryRepo.findAll();
        return CategoryMapper.INSTANCE.toResponses(categories);
    }

    @Override
    public CategoryResponse getById(UUID id) {
        var category = findById(id);
        return CategoryMapper.INSTANCE.toResponse(category);
    }

    @Override
    public Category findById(UUID id) {
        return categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND_MESSAGE,
                        CATEGORY_NOT_FOUND_CODE));
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        var category = findById(id);
        categoryRepo.delete(category);
    }
}
