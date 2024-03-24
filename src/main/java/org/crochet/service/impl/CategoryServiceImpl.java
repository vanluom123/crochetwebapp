package org.crochet.service.impl;

import org.crochet.properties.MessageCodeProperties;
import org.crochet.exception.IllegalArgumentException;
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

import static org.crochet.constant.MessageConstant.*;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepo categoryRepo;
    private final MessageCodeProperties msgCodeProps;

    public CategoryServiceImpl(CategoryRepo categoryRepo, MessageCodeProperties msgCodeProps) {
        this.categoryRepo = categoryRepo;
        this.msgCodeProps = msgCodeProps;
    }

    @Transactional
    @Override
    public List<CategoryResponse> create(CategoryCreationRequest request) {
        // Extract parent IDs and category name from the request
        String name = request.getName();

        // Check if a category with the same name already exists as a parent
        if (categoryRepo.existsByNameAndParentIsNull(name)) {
            throw new IllegalArgumentException(EXISTS_AS_A_PARENT_MESSAGE,
                    msgCodeProps.getCode("EXISTS_AS_A_PARENT_MESSAGE"));
        }
        // Retrieve parent categories from the database
        List<Category> parents = categoryRepo.findAllById(request.getParentIds());

        // Create child categories and add them to their respective parents
        Set<Category> children = new HashSet<>();

        if (parents.isEmpty()) {
            if (categoryRepo.existsByNameAndParentIsNotNull(name)) {
                throw new IllegalArgumentException(EXISTS_AS_A_CHILD_MESSAGE,
                        msgCodeProps.getCode("EXISTS_AS_A_CHILD_MESSAGE"));
            }
            // Create a new category
            Category category = new Category();
            category.setName(name);
            children.add(category);
        } else {
            for (Category parent : parents) {
                // Check if a child category with the same name already exists
                if (parent.getChildren().stream().anyMatch(child -> child.getName().equals(name))) {
                    // Skip this parent if a child with the same name already exists
                    continue;
                }
                // Create a new category
                Category category = new Category();
                category.setName(name);
                category.setParent(parent);
                children.add(category);
            }
        }

        if (children.isEmpty()) {
            throw new IllegalArgumentException(EXISTS_AS_A_CHILD_MESSAGE,
                    msgCodeProps.getCode("EXISTS_AS_A_CHILD_MESSAGE"));
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
        String name = request.getName();
        if (categoryRepo.existsByNameAndParentIsNull(name)) {
            throw new IllegalArgumentException(EXISTS_AS_A_PARENT_MESSAGE,
                    msgCodeProps.getCode("EXISTS_AS_A_PARENT_MESSAGE"));
        }
        if (category.getChildren().stream().anyMatch(c -> c.getName().equals(name))) {
            throw new IllegalArgumentException(EXISTS_AS_A_CHILD_MESSAGE,
                    msgCodeProps.getCode("EXISTS_AS_A_CHILD_MESSAGE"));
        }
        category.setName(name);
        category = categoryRepo.save(category);
        return CategoryMapper.INSTANCE.toResponse(category);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        var categories = categoryRepo.findAll();
        // get categories with no parent
        var parentCategories = categories.stream()
                .filter(category -> category.getParent() == null)
                .toList();
        return CategoryMapper.INSTANCE.toResponses(parentCategories);
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
                        msgCodeProps.getCode("CATEGORY_NOT_FOUND_MESSAGE")));
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        var category = findById(id);
        categoryRepo.delete(category);
    }
}
