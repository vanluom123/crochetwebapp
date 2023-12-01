package org.crochet.controller;

import jakarta.validation.Valid;
import org.crochet.request.ProductCategoryRequest;
import org.crochet.response.ProductCategoryResponse;
import org.crochet.service.contact.ProductCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductCategoryController {
    private final ProductCategoryService productCategoryService;

    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductCategoryResponse> createProduct(@Valid @RequestBody ProductCategoryRequest request) {
        var response = productCategoryService.createOrUpdate(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<ProductCategoryResponse>> getAll() {
        var responses = productCategoryService.getAll();
        return ResponseEntity.ok(responses);
    }
}
