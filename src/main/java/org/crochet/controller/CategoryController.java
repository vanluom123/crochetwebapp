package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import org.crochet.payload.request.CategoryCreationRequest;
import org.crochet.payload.request.CategoryUpdateRequest;
import org.crochet.payload.response.CategoryResponse;
import org.crochet.payload.response.ResponseData;
import org.crochet.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Create category with parent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseData.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseData.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseData.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseData.class))})
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/create")
    public ResponseEntity<List<CategoryResponse>> create(@Valid @RequestBody CategoryCreationRequest request) {
        var response = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update category with parent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseData.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseData.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseData.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseData.class))})
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = "BearerAuth")
    @PutMapping("/update")
    public ResponseEntity<CategoryResponse> update(@Valid @RequestBody CategoryUpdateRequest request) {
        var response = categoryService.update(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all categories")
    @ApiResponse(responseCode = "200", description = "Get all categories",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class))})
    @GetMapping("/get-all-categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        var response = categoryService.getAllCategories();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get category by id")
    @ApiResponse(responseCode = "200", description = "Get category by id",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponse.class))})
    @GetMapping("/getById")
    public ResponseEntity<CategoryResponse> getById(@RequestParam String id) {
        var response = categoryService.getById(id);
        return ResponseEntity.ok(response);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete category")
    @ApiResponse(responseCode = "200", description = "Category deleted",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
    @ApiResponse(responseCode = "400", description = "Bad request",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseData.class))})
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseData<String> delete(@RequestParam String id) {
        categoryService.delete(id);
        return ResponseData.<String>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Success")
                .data("Category deleted")
                .build();
    }
}
