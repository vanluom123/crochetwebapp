package org.crochet.mapper;

import org.crochet.model.ProductFile;
import org.crochet.response.ProductFileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {ProductMapper.class})
public interface ProductFileMapper {
    ProductFileMapper INSTANCE = Mappers.getMapper(ProductFileMapper.class);

    ProductFileResponse toResponse(ProductFile productFile);

    List<ProductFileResponse> toResponses(List<ProductFile> productFiles);
}