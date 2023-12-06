package org.crochet.mapper;

import org.crochet.model.Product;
import org.crochet.model.ProductFile;
import org.crochet.request.ProductRequest;
import org.crochet.response.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "id", source = "id", qualifiedByName = "uuidToString")
    @Mapping(target = "encodingBytes", source = "productFiles", qualifiedByName = "toList")
    ProductResponse toResponse(Product product);

    @Named("toList")
    default List<String> toList(Collection<ProductFile> productFiles) {
        return Optional.ofNullable(productFiles)
                .map(file -> file.stream()
                        .map(ProductFile::getBytes)
                        .toList())
                .orElseThrow(() -> new IllegalArgumentException("Input list cannot be null"));
    }

    List<ProductResponse> toResponses(Collection<Product> products);

    Product toEntity(ProductRequest request);

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