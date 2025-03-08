package org.crochet.service;

import org.crochet.model.Product;
import org.crochet.payload.request.ProductRequest;
import org.crochet.payload.response.ProductPaginationResponse;
import org.crochet.payload.response.ProductResponse;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ProductService {
    void createOrUpdate(ProductRequest request);

    ProductPaginationResponse getProducts(int pageNo, int pageSize, String sortBy, String sortDir, String categoryId, Specification<Product> spec);

    List<ProductResponse> getLimitedProducts();

    ProductResponse getDetail(String id);

    void delete(String id);

    List<String> getProductIds(int pageNo, int limit);

    void deleteMultiple(List<String> ids);
}
