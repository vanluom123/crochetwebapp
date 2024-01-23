package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.crochet.payload.request.CategoryCreationRequest;
import org.crochet.payload.request.CategoryCreationWithParentRequest;
import org.crochet.payload.request.CategoryUpdateRequest;
import org.crochet.payload.response.CategoryResponse;
import org.crochet.service.contact.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @Operation(summary = "Create category with parent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("create-with-parent")
    public ResponseEntity<CategoryResponse> createCategoryWithParent(
            @Valid @RequestBody
            CategoryCreationWithParentRequest request
    ) {
        var response = categoryService.createWithParent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Create category without parent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("create-not-parent")
    public ResponseEntity<CategoryResponse> createCategoryNotParent(
            @Valid @RequestBody
            CategoryCreationRequest request
    ) {
        var response = categoryService.createNotParent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update category with parent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @PutMapping("update-not-parent")
    public ResponseEntity<CategoryResponse> updateCategory(
            @Valid @RequestBody
            CategoryUpdateRequest request
    ) {
        var response = categoryService.updateNotParent(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update category without parent")
    @ApiResponse(responseCode = "200", description = "Get parent categories")
    @GetMapping("get-parent-categories")
    public ResponseEntity<List<CategoryResponse>> getParentCategories() {
        var response = categoryService.getParentCategories();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get sub categories")
    @ApiResponse(responseCode = "200", description = "Get sub categories")
    @GetMapping("get-sub-categories")
    public ResponseEntity<List<CategoryResponse>> getSubCategories(UUID parentId) {
        var response = categoryService.getSubCategories(parentId);
        return ResponseEntity.ok(response);
    }
}
