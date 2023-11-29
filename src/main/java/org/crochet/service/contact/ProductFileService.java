package org.crochet.service.contact;

import org.crochet.response.ProductFileResponse;

import java.util.List;

public interface ProductFileService {
    List<ProductFileResponse> create(List<String> fileNames, String productId);
}
