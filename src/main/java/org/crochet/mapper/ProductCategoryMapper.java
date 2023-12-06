package org.crochet.mapper;

import org.crochet.model.ProductCategory;
import org.crochet.response.ProductCategoryResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductCategoryMapper {
    ProductCategoryMapper INSTANCE = Mappers.getMapper(ProductCategoryMapper.class);

    ProductCategory toEntity(ProductCategoryResponse productCategoryResponse);

    ProductCategoryResponse toResponse(ProductCategory productCategory);

    default List<ProductCategoryResponse> toResponses(Collection<ProductCategory> categories) {
        return Optional.ofNullable(categories)
                .map(categoryGroup -> categoryGroup.stream()
                        .map(this::toResponse)
                        .toList())
                .orElseThrow(() -> new IllegalArgumentException("Input list cannot be null"));
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ProductCategory partialUpdate(ProductCategoryResponse productCategoryResponse, @MappingTarget ProductCategory productCategory);
}