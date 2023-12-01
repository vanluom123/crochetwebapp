package org.crochet.mapper;

import org.crochet.model.ProductFile;
import org.crochet.response.ProductFileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.List;

@Mapper(uses = {ProductMapper.class})
public interface ProductFileMapper {
    ProductFileMapper INSTANCE = Mappers.getMapper(ProductFileMapper.class);

    ProductFileResponse toResponse(ProductFile productFile);

    default List<ProductFileResponse> toResponses(Collection<ProductFile> productFiles) {
        if (ObjectUtils.isEmpty(productFiles)) {
            return null;
        }
        return productFiles.stream()
                .map(this::toResponse)
                .toList();
    }
}