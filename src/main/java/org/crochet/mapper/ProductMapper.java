package org.crochet.mapper;

import org.crochet.model.Product;
import org.crochet.model.ProductFile;
import org.crochet.request.ProductRequest;
import org.crochet.response.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "id", source = "id", qualifiedByName = "uuidToString")
    @Mapping(target = "encodingBytes", source = "productFiles", qualifiedByName = "toList")
    ProductResponse toResponse(Product product);

    @Named("toList")
    default List<String> toList(Collection<ProductFile> productFiles) {
        return productFiles.stream()
                .map(ProductFile::getBytes)
                .toList();
    }

    List<ProductResponse> toResponses(Collection<Product> products);

    Product toProduct(ProductRequest request);

    default String encoding(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    default byte[] decoding(String data) {
        return Base64.getDecoder().decode(data);
    }

    @Named("uuidToString")
    default String uuidToString(UUID uuid) {
        return uuid.toString();
    }
}