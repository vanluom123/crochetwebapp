package org.crochet.service.contact;

import org.crochet.payload.request.ProductRequest;
import org.crochet.payload.response.ProductPaginationResponse;
import org.crochet.payload.response.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponse createOrUpdate(ProductRequest request);

    ProductPaginationResponse getProducts(int pageNo, int pageSize, String sortBy, String sortDir, String text);

    List<ProductResponse> getLimitedProducts();

    ProductResponse getDetail(UUID id);
}
