package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.crochet.constant.AppConstant;
import org.crochet.enumerator.CurrencyCode;
import org.crochet.payload.request.ProductRequest;
import org.crochet.payload.response.ProductPaginationResponse;
import org.crochet.payload.response.ProductResponse;
import org.crochet.service.contact.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<ProductResponse> createProduct(@RequestParam(value = "id", required = false) String id,
                                                         @RequestParam("categoryId") String categoryId,
                                                         @RequestParam(value = "name", required = false) String name,
                                                         @RequestParam(value = "description", required = false) String description,
                                                         @RequestParam(value = "price") double price,
                                                         @RequestParam(value = "currency_code") String currencyCode,
                                                         @RequestPart(required = false) List<MultipartFile> files) {
        var request = ProductRequest.builder()
                .id(id)
                .productCategoryId(categoryId)
                .name(name)
                .description(description)
                .price(price)
                .currencyCode(CurrencyCode.valueOf(currencyCode))
                .build();
        var response = productService.createOrUpdate(request, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get a list of products")
    @ApiResponse(responseCode = "200", description = "List of products",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductPaginationResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @GetMapping("/pagination")
    public ResponseEntity<ProductPaginationResponse> getProducts(
            @RequestParam(value = "pageNo", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER,
                    required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE,
                    required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstant.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstant.DEFAULT_SORT_DIRECTION,
                    required = false) String sortDir,
            @RequestParam(value = "text", required = false) String text) {
        var response = productService.getProducts(pageNo, pageSize, sortBy, sortDir, text);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get product details by ID")
    @ApiResponse(responseCode = "200",
            description = "Product details retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductResponse.class)))
    @ApiResponse(responseCode = "404", description = "Product not found")
    @GetMapping("/detail")
    public ResponseEntity<ProductResponse> getProductDetail(@RequestParam("id") String id) {
        return ResponseEntity.ok(productService.getDetail(id));
    }
}
