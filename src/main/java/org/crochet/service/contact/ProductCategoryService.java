package org.crochet.service.contact;

import org.crochet.request.ProductCategoryRequest;
import org.crochet.response.ProductCategoryResponse;

import java.util.List;

public interface ProductCategoryService {
    ProductCategoryResponse createOrUpdate(ProductCategoryRequest request);

    List<ProductCategoryResponse> getAll();
}
