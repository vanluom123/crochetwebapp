package org.crochet.mapper;

import org.crochet.model.BlogFile;
import org.crochet.model.BlogPost;
import org.crochet.response.BlogPostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BlogPostMapper {
    BlogPostMapper INSTANCE = Mappers.getMapper(BlogPostMapper.class);

    @Mapping(target = "id", source = "id", qualifiedByName = "uuidToString")
    @Mapping(target = "bytes", source = "blogFiles", qualifiedByName = "toList")
    BlogPostResponse toResponse(BlogPost blogPost);

    @Named("toList")
    default List<String> toList(Collection<BlogFile> blogFiles) {
        return Optional.ofNullable(blogFiles)
                .map(blog -> blog.stream()
                        .map(BlogFile::getBytes)
                        .toList())
                .orElse(null);
    }

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