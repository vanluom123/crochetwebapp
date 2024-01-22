package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.crochet.payload.request.ProductCategoryRequest;
import org.crochet.payload.response.ProductCategoryResponse;
import org.crochet.payload.response.ProductCategoryResponseDto;
import org.crochet.service.contact.ProductCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product-category")
public class ProductCategoryController {
    private final ProductCategoryService productCategoryService;

    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    @Operation(summary = "Create a product category")
    @ApiResponse(responseCode = "201", description = "Product category created successfully",
            content = @Content(mediaType = "application/json"))
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<ProductCategoryResponseDto> createCategory(
            @Valid @RequestBody ProductCategoryRequest request
    ) {
        var response = productCategoryService.createOrUpdate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get all product categories")
    @ApiResponse(responseCode = "200", description = "List of all product categories",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @GetMapping("/getAll")
    public ResponseEntity<List<ProductCategoryResponse>> getAll() {
        var responses = productCategoryService.getAll();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get all product categories")
    @ApiResponse(responseCode = "200", description = "List of all product categories",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @GetMapping("/getCategories")
    public ResponseEntity<List<ProductCategoryResponseDto>> getCategories() {
        var responses = productCategoryService.getCategories();
        return ResponseEntity.ok(responses);
    }
}
