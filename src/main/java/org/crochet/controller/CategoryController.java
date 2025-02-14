package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.crochet.enums.ResultCode;
import org.crochet.payload.request.CategoryCreationRequest;
import org.crochet.payload.request.CategoryUpdateRequest;
import org.crochet.payload.response.CategoryResponse;
import org.crochet.payload.response.ResponseData;
import org.crochet.service.CategoryService;
import org.crochet.util.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Create category with parent")
    @ApiResponse(responseCode = "201", description = "Category created")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "BearerAuth")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseData<List<CategoryResponse>> create(@Valid @RequestBody CategoryCreationRequest request) {
        var response = categoryService.create(request);
        return ResponseUtil.success(response);
    }

    @Operation(summary = "Update category with parent")
    @ApiResponse(responseCode = "200", description = "Category updated",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponse.class))})
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "BearerAuth")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public ResponseData<CategoryResponse> update(@Valid @RequestBody CategoryUpdateRequest request) {
        var response = categoryService.update(request);
        return ResponseUtil.success(response);
    }

    @Operation(summary = "Get all categories")
    @ApiResponse(responseCode = "200", description = "Get all categories",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class))})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ResponseData<List<CategoryResponse>> getAllCategories() {
        var response = categoryService.getAllCategories();
        return ResponseUtil.success(response);
    }

    @Operation(summary = "Get category by id")
    @ApiResponse(responseCode = "200", description = "Get category by id",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponse.class))})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ResponseData<CategoryResponse> getById(@PathVariable("id") String id) {
        var response = categoryService.getById(id);
        return ResponseUtil.success(response);
    }

    @Operation(summary = "Delete category")
    @ApiResponse(responseCode = "200", description = "Category deleted",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseData<String> delete(@RequestParam("id") String id) {
        categoryService.delete(id);
        return ResponseUtil.success(ResultCode.MSG_DELETE_SUCCESS.message());
    }
}
