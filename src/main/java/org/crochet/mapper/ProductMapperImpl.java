package org.crochet.mapper;

import org.crochet.model.Product;
import org.crochet.response.ProductResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
public class ProductMapperImpl implements ProductMapper {
  @Override
  public ProductResponse toResponse(Product product) {
    if (product == null) {
      return null;
    }

    return ProductResponse.builder()
        .name(product.getName())
        .image(decoding(product.getImage()))
        .description(product.getDescription())
        .price(product.getPrice())
        .build();
  }

  @Override
  public Product toProduct(ProductResponse response) {
    if (response == null) {
      return null;
    }

    return Product.builder()
        .id(response.getId())
        .name(response.getName())
        .image(encoding(response.getImage()))
        .description(response.getDescription())
        .price(response.getPrice())
        .build();
  }

  @Override
  public List<ProductResponse> toResponses(List<Product> products) {
    if (ObjectUtils.isEmpty(products)) {
      return null;
    }

    return products.stream()
        .map(this::toResponse)
        .toList();
  }
}
