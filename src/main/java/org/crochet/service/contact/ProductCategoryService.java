package org.crochet.service.contact;

import org.crochet.payload.request.ProductCategoryRequest;
import org.crochet.payload.response.ProductCategoryResponse;
import org.crochet.payload.response.ProductCategoryResponseDto;

import java.util.List;

public interface ProductCategoryService {
    ProductCategoryResponseDto createOrUpdate(ProductCategoryRequest request);

    List<ProductCategoryResponse> getAll();

    List<ProductCategoryResponseDto> getCategories();
}
