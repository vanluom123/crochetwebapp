package org.crochet.controller;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.crochet.constant.AppConstant;
import org.crochet.enums.ResultCode;
import org.crochet.model.Product;
import org.crochet.payload.request.ProductRequest;
import org.crochet.payload.response.PaginationResponse;
import org.crochet.payload.response.ProductResponse;
import org.crochet.payload.response.ResponseData;
import org.crochet.service.ProductService;
import org.crochet.util.ResponseUtil;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Create a new product")
    @ApiResponse(responseCode = "201", description = "Product created successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductResponse.class)))
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseData<String> createProduct(@RequestBody ProductRequest request) {
        productService.createOrUpdate(request);
        return ResponseUtil.success(ResultCode.MSG_CREATE_OR_UPDATE_SUCCESS.message());
    }

    @Operation(summary = "Get a list of products")
    @ApiResponse(responseCode = "200", description = "List of products",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PaginationResponse.class)))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ResponseData<PaginationResponse<ProductResponse>> getProducts(
            @Parameter(description = "Page number")
            @RequestParam(value = "pageNo", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER,
                    required = false) int pageNo,
            @Parameter(description = "Page size")
            @RequestParam(value = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE,
                    required = false) int pageSize,
            @Parameter(description = "Sort by field")
            @RequestParam(value = "sortBy", defaultValue = AppConstant.DEFAULT_SORT_BY,
                    required = false) String sortBy,
            @Parameter(description = "Sort direction")
            @RequestParam(value = "sortDir", defaultValue = AppConstant.DEFAULT_SORT_DIRECTION,
                    required = false) String sortDir,
            @RequestParam(value = "categoryId", required = false) String categoryId,
            @Filter Specification<Product> spec) {
        var response = productService.getProducts(pageNo, pageSize, sortBy, sortDir, categoryId, spec);
        return ResponseUtil.success(response);
    }

    @Operation(summary = "Get product details by ID")
    @ApiResponse(responseCode = "200",
            description = "Product details retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductResponse.class)))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ResponseData<ProductResponse> getProductDetail(@PathVariable("id") String id) {
        var res = productService.getDetail(id);
        return ResponseUtil.success(res);
    }

    @Operation(summary = "Delete a product by ID")
    @ApiResponse(responseCode = "200",
            description = "Product deleted successfully",
            content = @Content(mediaType = "application/json"))
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseData<String> deleteProduct(@PathVariable("id") String id) {
        productService.delete(id);
        return ResponseUtil.success(ResultCode.MSG_DELETE_SUCCESS.message());
    }

    @Operation(summary = "Delete multiple products")
    @ApiResponse(responseCode = "200",
            description = "Products deleted successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseData.class)))
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseData<String> deleteMultipleProducts(@RequestBody List<String> ids) {
        productService.deleteMultiple(ids);
        return ResponseUtil.success(ResultCode.MSG_DELETE_SUCCESS.message());
    }

    @Operation(summary = "Get product ids")
    @ApiResponse(responseCode = "200", description = "List of product ids",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = List.class)))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/ids")
    public ResponseData<List<String>> getProductIds(
            @Parameter(description = "Page number (default: 0)")
            @RequestParam(value = "pageNo", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER,
                    required = false) int pageNo,
            @Parameter(description = "Limit (default: 48)")
            @RequestParam(value = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE, required = false) int pageSize) {
        var response = productService.getProductIds(pageNo, pageSize);
        return ResponseUtil.success(response);
    }
}
