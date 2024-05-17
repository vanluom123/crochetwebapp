package org.crochet.mapper;

import org.crochet.model.BlogCategory;
import org.crochet.payload.request.BlogCategoryRequest;
import org.crochet.payload.response.BlogCategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {BlogPostMapper.class}
)
public interface BlogCategoryMapper extends AbstractMapper<BlogCategory, BlogCategoryResponse>, PartialUpdate<BlogCategory, BlogCategoryRequest> {
    BlogCategoryMapper INSTANCE = Mappers.getMapper(BlogCategoryMapper.class);
}