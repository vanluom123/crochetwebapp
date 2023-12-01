package org.crochet.service.contact;

import org.crochet.response.ProductFileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductFileService {
    List<ProductFileResponse> create(MultipartFile[] files, String productId);
}
