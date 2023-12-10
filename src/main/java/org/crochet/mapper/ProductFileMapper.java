package org.crochet.mapper;

import org.crochet.model.ProductFile;
import org.crochet.response.ProductFileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Mapper(uses = {ProductMapper.class})
public interface ProductFileMapper {
    ProductFileMapper INSTANCE = Mappers.getMapper(ProductFileMapper.class);

    ProductFileResponse toResponse(ProductFile productFile);

    default List<ProductFileResponse> toResponses(Collection<ProductFile> productFiles) {
        return Optional.ofNullable(productFiles)
                .map(file -> file.stream()
                        .map(this::toResponse)
                        .toList())
                .orElse(null);
    }
}