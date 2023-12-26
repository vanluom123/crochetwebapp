package org.crochet.mapper;

import org.crochet.model.ProductCategory;
import org.crochet.payload.response.ProductCategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductCategoryMapper {
    ProductCategoryMapper INSTANCE = Mappers.getMapper(ProductCategoryMapper.class);

    ProductCategoryResponse toResponse(ProductCategory productCategory);

    default List<ProductCategoryResponse> toResponses(Collection<ProductCategory> categories) {
        return Optional.ofNullable(categories)
                .map(categoryGroup -> categoryGroup.stream()
                        .map(this::toResponse)
                        .toList())
                .orElse(null);
    }
}