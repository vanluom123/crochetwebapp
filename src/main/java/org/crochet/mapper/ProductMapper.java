package org.crochet.mapper;

import org.crochet.model.Product;
import org.crochet.response.ProductResponse;

import java.util.Base64;
import java.util.List;

public interface ProductMapper {
  ProductResponse toResponse(Product product);

  List<ProductResponse> toResponses(List<Product> products);

  default String encoding(byte[] data) {
    return Base64.getEncoder().encodeToString(data);
  }

  default byte[] decoding(String data) {
    return Base64.getDecoder().decode(data);
  }
}
