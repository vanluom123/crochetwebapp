package org.crochet.service;

import org.crochet.model.Product;
import org.crochet.payload.request.ProductRequest;
import org.crochet.payload.response.PaginationResponse;
import org.crochet.payload.response.ProductResponse;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ProductService {
    void createOrUpdate(ProductRequest request);

    PaginationResponse<ProductResponse> getProducts(int offset, int limit, String sortBy, String sortDir, String categoryId, Specification<Product> spec);

    List<ProductResponse> getLimitedProducts();

    ProductResponse getDetail(String id);

    Product findById(String id);

    void delete(String id);

    List<String> getProductIds(int offset, int limit);

    void deleteMultiple(List<String> ids);
}
