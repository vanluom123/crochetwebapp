package org.crochet.mapper;

import org.crochet.model.Product;
import org.crochet.response.ProductResponse;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Base64;
import java.util.List;

@Mapper
public interface ProductMapper {
  ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

  @InheritInverseConfiguration
  @Mapping(target = "image", source = "image", qualifiedByName = "decoding")
  ProductResponse toResponse(Product product);

  @Mapping(target = "image", source = "image", qualifiedByName = "encoding")
  Product toProduct(ProductResponse item);

  List<ProductResponse> toResponses(List<Product> products);

  @Named("encoding")
  default String encoding(byte[] data) {
    return Base64.getEncoder().encodeToString(data);
  }

  @Named("decoding")
  default byte[] decoding(String data) {
    return Base64.getDecoder().decode(data);
  }
}
