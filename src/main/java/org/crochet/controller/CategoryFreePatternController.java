package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.crochet.exception.ApiError;
import org.crochet.payload.request.CategoryCreationRequest;
import org.crochet.payload.request.CategoryUpdateRequest;
import org.crochet.payload.response.CategoryResponse;
import org.crochet.service.CategoryPatternService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/category-free-pattern")
public class CategoryFreePatternController {
    private final CategoryPatternService categoryPatternService;

    public CategoryFreePatternController(CategoryPatternService categoryPatternService) {
        this.categoryPatternService = categoryPatternService;
    }

    @Operation(summary = "Create category with parent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))})
    })
//    @PreAuthorize("hasRole('ADMIN')")
//    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/create")
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryCreationRequest request) {
        var response = categoryPatternService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update category with parent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))})
    })
//    @PreAuthorize("hasRole('ADMIN')")
//    @SecurityRequirement(name = "BearerAuth")
    @PutMapping("/update")
    public ResponseEntity<CategoryResponse> update(@Valid @RequestBody CategoryUpdateRequest request) {
        var response = categoryPatternService.update(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update category without parent")
    @ApiResponse(responseCode = "200", description = "Get parent categories",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class))})
    @GetMapping("/get-parent-categories")
    public ResponseEntity<List<CategoryResponse>> getParentCategories() {
        var response = categoryPatternService.getParentCategories();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get sub categories")
    @ApiResponse(responseCode = "200", description = "Get sub categories",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class))})
    @GetMapping("/get-sub-categories")
    public ResponseEntity<List<CategoryResponse>> getSubCategories(UUID parentId) {
        var response = categoryPatternService.getSubCategories(parentId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete category")
    @ApiResponse(responseCode = "200", description = "Category deleted",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
    @ApiResponse(responseCode = "400", description = "Bad request",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))})
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(UUID id) {
        categoryPatternService.delete(id);
        return ResponseEntity.ok("Category deleted");
    }
}
