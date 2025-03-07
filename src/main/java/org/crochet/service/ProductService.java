package org.crochet.service;

import org.crochet.payload.request.Filter;
import org.crochet.payload.request.ProductRequest;
import org.crochet.payload.response.ProductPaginationResponse;
import org.crochet.payload.response.ProductResponse;

import java.util.List;

public interface ProductService {
    void createOrUpdate(ProductRequest request);

    ProductPaginationResponse getProducts(int pageNo, int pageSize, String sortBy, String sortDir, Filter[] filters);

    List<ProductResponse> getLimitedProducts();

    ProductResponse getDetail(String id);

    void delete(String id);

    List<String> getProductIds(int pageNo, int limit);

    void deleteMultiple(List<String> ids);
}
