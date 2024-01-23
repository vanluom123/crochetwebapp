package org.crochet.service;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.CategoryMapper;
import org.crochet.model.Category;
import org.crochet.payload.request.CategoryCreationRequest;
import org.crochet.payload.request.CategoryCreationWithParentRequest;
import org.crochet.payload.request.CategoryUpdateRequest;
import org.crochet.payload.response.CategoryResponse;
import org.crochet.repository.CategoryRepo;
import org.crochet.service.contact.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
    public CategoryResponse createWithParent(CategoryCreationWithParentRequest request) {
        /*
            Create child with parent
            1. Check parent is existed
                1.1. If existed, create child with parent
                1.2. If not existed, create parent and create child with parent
         */
        Category parent = findByName(request.getParentName())
                .orElseGet(() -> {
                    var newParent = new Category();
                    newParent.setName(request.getParentName());
                    return categoryRepo.save(newParent);
                }); // 1.2
        var child = new Category();
        child.setName(request.getName());
        child.setParent(parent);
        child = categoryRepo.save(child);
        return categoryMapper.toResponse(child);
    }

    @Transactional
    @Override
    public CategoryResponse createNotParent(CategoryCreationRequest request) {
        var category = new Category();
        category.setName(request.getName());
        category = categoryRepo.save(category);
        return categoryMapper.toResponse(category);
    }

    @Transactional
    @Override
    public CategoryResponse updateNotParent(CategoryUpdateRequest request) {
        var category = findById(request.getId());
        category.setName(request.getName());
        category = categoryRepo.save(category);
        return categoryMapper.toResponse(category);
    }

    public Optional<Category> findByName(String name) {
        return categoryRepo.findByName(name);
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
