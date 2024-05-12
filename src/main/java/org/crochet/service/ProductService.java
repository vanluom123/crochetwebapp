package org.crochet.service;

import org.crochet.payload.request.ProductRequest;
import org.crochet.payload.response.ProductPaginationResponse;
import org.crochet.payload.response.ProductResponse;
import org.crochet.repository.Filter;

import java.util.List;

public interface ProductService {
    ProductResponse createOrUpdate(ProductRequest request);

    ProductPaginationResponse getProducts(int pageNo, int pageSize, String sortBy, String sortDir, String searchText, String categoryId, List<Filter> filters);

    List<ProductResponse> getLimitedProducts();

    ProductResponse getDetail(String id);

    void delete(String id);
}
