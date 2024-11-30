package org.crochet.service.impl;

import lombok.RequiredArgsConstructor;
import org.crochet.exception.IllegalArgumentException;
import org.crochet.mapper.CategoryMapper;
import org.crochet.model.Category;
import org.crochet.payload.request.CategoryCreationRequest;
import org.crochet.payload.request.CategoryUpdateRequest;
import org.crochet.payload.response.CategoryResponse;
import org.crochet.repository.CategoryRepo;
import org.crochet.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.crochet.constant.MessageCodeConstant.MAP_CODE;
import static org.crochet.constant.MessageConstant.ERROR_CHILD_CATEGORY_EXISTS;
import static org.crochet.constant.MessageConstant.ERROR_PARENT_CATEGORY_EXISTS;
import static org.crochet.constant.MessageConstant.MSG_CATEGORY_NOT_FOUND;
import static org.crochet.constant.MessageConstant.MSG_DUPLICATE_CATEGORY_NAME_UNDER_PROVIDED_PARENTS;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepo categoryRepo;

    /**
     * Create a new category or multiple categories
     *
     * @param request the request object containing the parent IDs and category name
     * @return a list of CategoryResponse objects
     */
    @Transactional
    @Override
    public List<CategoryResponse> create(CategoryCreationRequest request) {
        String name = request.getName();

        // Check if the category already exists as a root (parent) category
        if (categoryRepo.existsByNameAndParentIsNull(name)) {
            throw new IllegalArgumentException(ERROR_PARENT_CATEGORY_EXISTS,
                    MAP_CODE.get(ERROR_PARENT_CATEGORY_EXISTS));
        }

        // Fetch parent categories based on the provided parent IDs
        List<Category> parents = categoryRepo.findAllById(request.getParentIds());

        List<Category> children = new ArrayList<>();

        if (parents.isEmpty()) {
            // If no parents are provided, create a root category
            if (categoryRepo.existsByNameAndParentIsNotNull(name)) {
                throw new IllegalArgumentException(ERROR_CHILD_CATEGORY_EXISTS,
                        MAP_CODE.get(ERROR_CHILD_CATEGORY_EXISTS));
            }
            Category category = new Category();
            category.setName(name);
            children.add(category);
        } else {
            for (Category parent : parents) {
                // Check for circular reference
                if (isCircularReference(parent, request)) {
                    continue;
                }

                // Check if a child category with the same name already exists under this parent
                if (parent.getChildren().stream().anyMatch(child -> child.getName().equals(name))) {
                    // Skip this parent if a child with the same name already exists
                    continue;
                }

                // Create a new child category under this parent
                Category category = new Category();
                category.setName(name);
                category.setParent(parent);
                children.add(category);
            }
        }

        // If no children were created, throw an error
        if (children.isEmpty()) {
            throw new IllegalArgumentException(MSG_DUPLICATE_CATEGORY_NAME_UNDER_PROVIDED_PARENTS,
                    MAP_CODE.get(MSG_DUPLICATE_CATEGORY_NAME_UNDER_PROVIDED_PARENTS));
        }

        // Save all new categories to the database
        List<Category> savedCategories = categoryRepo.saveAll(children);

        // Map child categories to CategoryResponse objects and return them
        return CategoryMapper.INSTANCE.toResponses(savedCategories);
    }

    // Recursive method to detect circular references
    private boolean isCircularReference(Category parent, CategoryCreationRequest request) {
        Category current = parent;
        while (current != null) {
            if (current.getName().equals(request.getName())) {  // Assuming uniqueness by name
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    /**
     * Update a category
     *
     * @param request the request object containing the category ID and new name
     * @return a CategoryResponse object
     */
    @Transactional
    @Override
    public CategoryResponse update(CategoryUpdateRequest request) {
        // Find the existing category by ID
        Category category = findById(request.getId());

        // Extract the new name from the request
        String newName = request.getName();

        // Check if the new name already exists as a parent category
        if (categoryRepo.existsByNameAndParentIsNull(newName)) {
            throw new IllegalArgumentException(ERROR_PARENT_CATEGORY_EXISTS,
                    MAP_CODE.get(ERROR_PARENT_CATEGORY_EXISTS));
        }

        // Check if the new name already exists as a child category under this category's parent
        if (category.getParent() != null && category.getParent().getChildren().stream()
                .anyMatch(c -> c.getName().equals(newName) && !c.getId().equals(request.getId()))) {
            throw new IllegalArgumentException(ERROR_CHILD_CATEGORY_EXISTS,
                    MAP_CODE.get(ERROR_CHILD_CATEGORY_EXISTS));
        }

        // Update the category's name
        category.setName(newName);

        // Save the updated category
        category = categoryRepo.save(category);

        // Map the updated category to a response object and return it
        return CategoryMapper.INSTANCE.toResponse(category);
    }

    /**
     * Get all categories
     *
     * @return a list of CategoryResponse objects
     */
    @Override
    public List<CategoryResponse> getAllCategories() {
        var categories = categoryRepo.getCategories();
        var parentCategories = categories.stream()
                .filter(category -> category.getParent() == null)
                .toList();
        return CategoryMapper.INSTANCE.toResponses(parentCategories);
    }

    /**
     * Get a category by ID
     *
     * @param id the category ID
     * @return a CategoryResponse object
     */
    @Override
    public CategoryResponse getById(String id) {
        var category = findById(id);
        return CategoryMapper.INSTANCE.toResponse(category);
    }

    /**
     * Get all child categories of a parent category
     *
     * @param id the parent category ID
     * @return a list of CategoryResponse objects
     */
    private Category findById(String id) {
        var categories = categoryRepo.findCategoriesByIds(id);
        if (categories.isEmpty()) {
            throw new IllegalArgumentException(MSG_CATEGORY_NOT_FOUND,
                    MAP_CODE.get(MSG_CATEGORY_NOT_FOUND));
        }
        return categories.get(0);
    }

    /**
     * Delete a category
     *
     * @param id the category ID
     */
    @Transactional
    @Override
    public void delete(String id) {
        var category = findById(id);
        categoryRepo.delete(category);
    }
}
