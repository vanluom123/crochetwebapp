package org.crochet.controller;

import org.crochet.constant.AppConstant;
import org.crochet.request.ProductRequest;
import org.crochet.response.ProductPaginationResponse;
import org.crochet.response.ProductResponse;
import org.crochet.service.contact.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        var response = productService.createOrUpdate(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pagination")
    public ResponseEntity<ProductPaginationResponse> getProducts(
            @RequestParam(value = "pageNo", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstant.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstant.DEFAULT_SORT_DIRECTION,
                    required = false) String sortDir,
            @RequestParam(value = "text", required = false) String text) {
        var response = productService.getProducts(pageNo, pageSize, sortBy, sortDir, text);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/detail")
    public ResponseEntity<ProductResponse> getProductDetail(@RequestParam("id") String id) {
        return ResponseEntity.ok(productService.getDetail(id));
    }
}
