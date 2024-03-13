package org.crochet.mapper;

import org.crochet.model.CategoryFreePattern;
import org.crochet.payload.response.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryFreePatternMapper {
    CategoryFreePatternMapper INSTANCE = Mappers.getMapper(CategoryFreePatternMapper.class);

    CategoryFreePattern toEntity(CategoryResponse categoryResponse);

    CategoryResponse toResponse(CategoryFreePattern category);

    List<CategoryResponse> toResponses(Collection<CategoryFreePattern> categories);
}