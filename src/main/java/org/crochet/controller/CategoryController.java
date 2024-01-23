package org.crochet.controller;

import jakarta.validation.Valid;
import org.crochet.payload.request.CategoryCreationRequest;
import org.crochet.payload.request.CategoryCreationWithParentRequest;
import org.crochet.payload.request.CategoryUpdateRequest;
import org.crochet.payload.response.CategoryResponse;
import org.crochet.service.contact.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("create-with-parent")
    public ResponseEntity<CategoryResponse> createCategoryWithParent(
            @Valid @RequestBody
            CategoryCreationWithParentRequest request
    ) {
        var response = categoryService.createWithParent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("create-not-parent")
    public ResponseEntity<CategoryResponse> createCategoryNotParent(
            @Valid @RequestBody
            CategoryCreationRequest request
    ) {
        var response = categoryService.createNotParent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("update-with-parent")
    public ResponseEntity<CategoryResponse> updateCategory(
            @Valid @RequestBody
            CategoryUpdateRequest request
    ) {
        var response = categoryService.updateNotParent(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("get-parent-categories")
    public ResponseEntity<List<CategoryResponse>> getParentCategories() {
        var response = categoryService.getParentCategories();
        return ResponseEntity.ok(response);
    }

    @GetMapping("get-sub-categories")
    public ResponseEntity<List<CategoryResponse>> getSubCategories(UUID parentId) {
        var response = categoryService.getSubCategories(parentId);
        return ResponseEntity.ok(response);
    }
}
