package org.crochet.service.contact;

import org.crochet.payload.request.ProductRequest;
import org.crochet.payload.response.ProductPaginationResponse;
import org.crochet.payload.response.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ProductResponse createOrUpdate(ProductRequest request, List<MultipartFile> files);

    ProductPaginationResponse getProducts(int pageNo, int pageSize, String sortBy, String sortDir, String text);

    List<ProductResponse> getLimitedProducts();

    ProductResponse getDetail(String id);
}
