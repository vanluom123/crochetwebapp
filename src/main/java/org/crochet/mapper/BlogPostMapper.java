package org.crochet.mapper;

import org.crochet.model.BlogPost;
import org.crochet.payload.request.BlogPostRequest;
import org.crochet.payload.response.BlogPostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {FileMapper.class})
public interface BlogPostMapper extends PartialUpdate<BlogPost, BlogPostRequest> {
    BlogPostMapper INSTANCE = Mappers.getMapper(BlogPostMapper.class);

    @Mapping(target = "id", source = "id", qualifiedByName = "uuidToString")
    BlogPostResponse toResponse(BlogPost blogPost);

    @Named("uuidToString")
    default String uuidToString(UUID uuid) {
        return uuid.toString();
    }

    default List<BlogPostResponse> toResponses(Collection<BlogPost> blogPosts) {
        return Optional.ofNullable(blogPosts)
                .map(file -> file.stream()
                        .map(this::toResponse)
                        .toList())
                .orElse(null);
    }
}