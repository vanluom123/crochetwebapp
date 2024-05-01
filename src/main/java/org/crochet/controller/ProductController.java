package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.crochet.constant.AppConstant;
import org.crochet.payload.request.ProductRequest;
import org.crochet.payload.response.ProductPaginationResponse;
import org.crochet.payload.response.ProductResponse;
import org.crochet.repository.Filter;
import org.crochet.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Create a new product")
    @ApiResponse(responseCode = "201", description = "Product created successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<ProductResponse> createProduct(
            @RequestBody ProductRequest request
    ) {
        var response = productService.createOrUpdate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @PostMapping("/batchInsert")
    public List<ProductResponse> batchInsert(@RequestBody List<ProductRequest> requests) {
        return productService.batchInsert(requests);
    }

    @Operation(summary = "Get a list of products")
    @ApiResponse(responseCode = "200", description = "List of products",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductPaginationResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @PostMapping("/pagination")
    public ResponseEntity<ProductPaginationResponse> getProducts(
            @Parameter(description = "Page number")
            @RequestParam(value = "pageNo", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER,
                    required = false) int pageNo,
            @Parameter(description = "Page size")
            @RequestParam(value = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE,
                    required = false) int pageSize,
            @Parameter(description = "Sort by field")
            @RequestParam(value = "sortBy", defaultValue = AppConstant.DEFAULT_SORT_BY, required = false) String sortBy,
            @Parameter(description = "Sort direction")
            @RequestParam(value = "sortDir", defaultValue = AppConstant.DEFAULT_SORT_DIRECTION,
                    required = false) String sortDir,
            @Parameter(description = "Search by name or description")
            @RequestParam(value = "searchText", required = false) String searchText,
            @Parameter(description = "Category ID")
            @RequestParam(value = "categoryId", required = false) UUID categoryId,
            @RequestBody(required = false) List<Filter> filters) {
        var response = productService.getProducts(pageNo, pageSize, sortBy, sortDir, searchText, categoryId, filters);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get product details by ID")
    @ApiResponse(responseCode = "200",
            description = "Product details retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductResponse.class)))
    @ApiResponse(responseCode = "404", description = "Product not found")
    @GetMapping("/detail")
    public ResponseEntity<ProductResponse> getProductDetail(@RequestParam("id") UUID id) {
        return ResponseEntity.ok(productService.getDetail(id));
    }

    @Operation(summary = "Delete a product by ID")
    @ApiResponse(responseCode = "200", description = "Product deleted successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(mediaType = "application/json"))
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<String> deleteProduct(@RequestParam("id") UUID id) {
        productService.delete(id);
        return ResponseEntity.ok("Product deleted successfully");
    }
}
