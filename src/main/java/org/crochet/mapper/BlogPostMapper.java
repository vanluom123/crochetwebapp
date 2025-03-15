package org.crochet.mapper;

import org.crochet.model.BlogPost;
import org.crochet.payload.request.BlogPostRequest;
import org.crochet.payload.response.BlogPostResponse;
import org.crochet.util.ImageUtils;
import org.crochet.util.ObjectUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {FileMapper.class}
)
public interface BlogPostMapper extends PartialUpdate<BlogPost, BlogPostRequest> {
    BlogPostMapper INSTANCE = Mappers.getMapper(BlogPostMapper.class);

    @Mapping(target = "isHome", source = "home")
    BlogPostResponse toResponse(BlogPost blogPost);

    default List<BlogPostResponse> toResponses(Collection<BlogPost> blogPosts) {
        return Optional.ofNullable(blogPosts)
                .map(blogs -> blogs.stream()
                        .map(this::toResponse)
                        .toList())
                .orElse(null);
    }

    @Override
    default BlogPost partialUpdate(BlogPostRequest request, BlogPost post) {
        if (post == null) {
            return null;
        }
        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }
        if (request.isHome() != post.isHome()) {
            post.setHome(request.isHome());
        }
        if (ObjectUtils.isNotEmpty(request.getFiles())) {
            var sortedFiles = ImageUtils.sortFiles(request.getFiles());
            post.setFiles(FileMapper.INSTANCE.toEntities(sortedFiles));
        }
        return post;
    }
}