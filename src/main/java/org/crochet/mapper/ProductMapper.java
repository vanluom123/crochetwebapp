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
import java.util.List;
import java.util.Set;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "fileNames", source = "productFiles", qualifiedByName = "covertToImages")
    ProductResponse toResponse(Product product);

    @Named("covertToImages")
    default List<String> convertToImages(Set<ProductFile> productFiles) {
        return productFiles.stream()
                .map(ProductFile::getFileUrl)
                .toList();
    }

    List<ProductResponse> toResponses(List<Product> products);

    default String encoding(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    default byte[] decoding(String data) {
        return Base64.getDecoder().decode(data);
    }

    Product toProduct(ProductRequest request);
}