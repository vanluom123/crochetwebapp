package org.crochet.mapper;

import org.crochet.model.Category;
import org.crochet.payload.response.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    Category toEntity(CategoryResponse categoryResponse);

    CategoryResponse toResponse(Category category);

    List<CategoryResponse> toResponses(Collection<Category> categories);
}