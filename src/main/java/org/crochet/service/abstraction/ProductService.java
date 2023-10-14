package org.crochet.service.abstraction;

import org.crochet.request.ProductRequest;
import org.crochet.response.ProductPaginationResponse;
import org.crochet.response.ProductResponse;

public interface ProductService {
  ProductResponse createOrUpdate(ProductRequest request);

  ProductPaginationResponse getProducts(int pageNo, int pageSize, String sortBy, String sortDir, String text);

  ProductResponse getDetail(long id);
}
