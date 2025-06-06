package org.crochet.service;

import org.crochet.model.Category;
import org.crochet.payload.request.CategoryCreationRequest;
import org.crochet.payload.response.CategoryResponse;
import org.crochet.repository.CategoryRepo;
import org.crochet.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepo categoryRepo;
    @Mock
    private PermissionService permissionService;

    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryServiceImpl(categoryRepo, permissionService);
    }

    @Test
    void testCreateRootCategorySuccessfully() {
        // Arrange
        CategoryCreationRequest request = new CategoryCreationRequest();
        request.setName("New Category");
        request.setParentIds(Collections.emptyList());

        // Mock the repository behavior
        when(categoryRepo.existsByNameAndParentIsNull("New Category")).thenReturn(false);
        when(categoryRepo.existsByNameAndParentIsNotNull("New Category")).thenReturn(false);

        // Create a new Category object that should be saved
        Category category = new Category();
        category.setId("category-id");
        category.setName("New Category");

        List<Category> savedCategories = List.of(category);
        when(categoryRepo.saveAll(anyList())).thenReturn(savedCategories);

        // Act
        List<CategoryResponse> responses = categoryService.create(request);

        // Assert
        assertNotNull(responses, "Responses should not be null");
        assertFalse(responses.isEmpty(), "Responses should not be empty");
        assertEquals(1, responses.size(), "There should be exactly one category response");
        assertEquals("New Category", responses.getFirst().getName(), "Category name should match");

        // Verify repository interactions
        verify(categoryRepo, times(1)).existsByNameAndParentIsNull("New Category");
        verify(categoryRepo, times(1)).saveAll(anyList());
    }

    @Test
    void testCreateChildCategorySuccessfully() {
        // Arrange
        Category parent = new Category();
        parent.setId("parent-id");
        parent.setName("Parent Category");

        CategoryCreationRequest request = new CategoryCreationRequest();
        request.setName("Child Category");
        request.setParentIds(List.of("parent-id"));

        when(categoryRepo.existsByNameAndParentIsNull("Child Category")).thenReturn(false);
        when(categoryRepo.findAllById(List.of("parent-id"))).thenReturn(List.of(parent));


        Category childCategory = new Category();
        childCategory.setName("Child Category 2");
        childCategory.setParent(parent);
        parent.setChildren(Set.of(childCategory));

        List<Category> savedCategories = List.of(childCategory);
        when(categoryRepo.saveAll(anyList())).thenReturn(savedCategories);

        // Act
        List<CategoryResponse> responses = categoryService.create(request);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Child Category 2", responses.getFirst().getName());

        // Verify repository interactions
        verify(categoryRepo, times(1)).findAllById(List.of("parent-id"));
        verify(categoryRepo, times(1)).saveAll(anyList());
    }
}

