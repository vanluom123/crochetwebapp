package org.crochet.controller;

import org.crochet.constant.AppConstant;
import org.crochet.request.ProductRequest;
import org.crochet.response.ProductPaginationResponse;
import org.crochet.response.ProductResponse;
import org.crochet.service.abstraction.FirebaseService;
import org.crochet.service.abstraction.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/product")
public class ProductController {
  private final ProductService productService;
  private final FirebaseService firebaseService;

  @Autowired
  public ProductController(ProductService productService, FirebaseService firebaseService) {
    this.productService = productService;
    this.firebaseService = firebaseService;
  }

  @PostMapping("/create")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<String> createProduct(@RequestParam("filePath") String filePath,
                                              @RequestBody ProductRequest request) {
    var byteData = firebaseService.getImage(filePath);
    var image = Base64.getEncoder().encodeToString(byteData);
    request.setImage(image);
    productService.createOrUpdate(request);
    return ResponseEntity.ok("Create product successfully");
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
  public ResponseEntity<ProductResponse> getProductDetail(@RequestParam("id") long id) {
    return ResponseEntity.ok(productService.getDetail(id));
  }
}
