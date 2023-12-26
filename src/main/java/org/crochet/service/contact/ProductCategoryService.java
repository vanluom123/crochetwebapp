package org.crochet.service.contact;

import org.crochet.payload.request.ProductCategoryRequest;
import org.crochet.payload.response.ProductCategoryResponse;

import java.util.List;

public interface ProductCategoryService {
    ProductCategoryResponse createOrUpdate(ProductCategoryRequest request);

    List<ProductCategoryResponse> getAll();
}
