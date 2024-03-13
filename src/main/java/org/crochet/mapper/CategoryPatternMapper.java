package org.crochet.mapper;

import org.crochet.model.CategoryPattern;
import org.crochet.payload.response.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryPatternMapper {
    CategoryPatternMapper INSTANCE = Mappers.getMapper(CategoryPatternMapper.class);

    CategoryPattern toEntity(CategoryResponse categoryResponse);

    CategoryResponse toResponse(CategoryPattern category);

    List<CategoryResponse> toResponses(Collection<CategoryPattern> categories);
}