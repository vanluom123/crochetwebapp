package org.crochet.service.contact;

import org.crochet.request.ProductRequest;
import org.crochet.response.ProductPaginationResponse;
import org.crochet.response.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse createOrUpdate(ProductRequest request);

    ProductPaginationResponse getProducts(int pageNo, int pageSize, String sortBy, String sortDir, String text);

    List<ProductResponse> getLimitedProducts();

    ProductResponse getDetail(String id);
}
