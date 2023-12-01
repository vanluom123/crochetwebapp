package org.crochet.mapper;

import org.crochet.model.ProductCategory;
import org.crochet.response.ProductCategoryResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductCategoryMapper {
    ProductCategory toEntity(ProductCategoryResponse productCategoryResponse);

    ProductCategoryResponse toResponse(ProductCategory productCategory);

    default List<ProductCategoryResponse> toResponses(Collection<ProductCategory> categories) {
        if (ObjectUtils.isEmpty(categories)) {
            return null;
        }
        return categories.stream()
                .map(this::toResponse)
                .toList();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ProductCategory partialUpdate(ProductCategoryResponse productCategoryResponse, @MappingTarget ProductCategory productCategory);
}