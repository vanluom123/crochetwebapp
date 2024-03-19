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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public List<CategoryResponse> create(CategoryCreationRequest request) {
        // Extract parent IDs and category name from the request
        List<UUID> parentIds = request.getParentIds();
        String name = request.getName();

        // Check if a category with the same name already exists as a parent
        List<Category> allCategories = categoryRepo.findAll();
        if (allCategories.parallelStream().anyMatch(category -> category.getName().equals(name) && category.getParent() == null)) {
            throw new IllegalArgumentException("A category with the same name already exists as a parent.");
        }

        // Retrieve parent categories from the database
        List<Category> parents = categoryRepo.findAllById(parentIds);

        // Convert parents list to a map for faster lookup
        Map<UUID, Category> parentMap = parents.parallelStream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));

        // Create a new category
        Category category = new Category();
        category.setName(name);

        // Create child categories and add them to their respective parents
        Set<Category> children = new HashSet<>();
        if (parentIds.isEmpty()) {
            children.add(category);
        } else {
            for (UUID parentId : parentIds) {
                if (parentMap.containsKey(parentId)) {
                    Category parent = parentMap.get(parentId);
                    category.setParent(parent);
                    // Check if a child category with the same name already exists
                    if (parent.getChildren().parallelStream().anyMatch(child -> child.getName().equals(name))) {
                        throw new IllegalArgumentException("A child category with the same name already exists within the parent category.");
                    }
                }
                children.add(category);
            }
        }

        // Save all categories to the database at once
        categoryRepo.saveAll(children);

        // Map child categories to CategoryResponse objects and return them
        return CategoryMapper.INSTANCE.toResponses(new ArrayList<>(children));
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
