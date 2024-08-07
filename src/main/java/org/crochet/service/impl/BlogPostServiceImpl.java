package org.crochet.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.BlogPostMapper;
import org.crochet.mapper.FileMapper;
import org.crochet.model.BlogCategory;
import org.crochet.model.BlogPost;
import org.crochet.payload.request.BlogPostRequest;
import org.crochet.payload.response.BlogPostPaginationResponse;
import org.crochet.payload.response.BlogPostResponse;
import org.crochet.repository.BlogCategoryRepo;
import org.crochet.repository.BlogPostRepository;
import org.crochet.repository.BlogPostSpecifications;
import org.crochet.service.BlogPostService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * BlogPostServiceImpl class
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BlogPostServiceImpl implements BlogPostService {
    final BlogPostRepository blogPostRepo;
    final BlogCategoryRepo blogCategoryRepo;

    /**
     * Creates a new blog post or updates an existing one based on the provided {@link BlogPostRequest}.
     * If the request contains an ID, it updates the existing blog post with the corresponding ID.
     * If the request does not contain an ID, it creates a new blog post.
     *
     * @param request The {@link BlogPostRequest} containing information for creating or updating the blog post.
     * @return A {@link BlogPostResponse} containing detailed information about the created or updated blog post.
     */
    @Transactional
    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "limitedblogs", allEntries = true),
                    @CacheEvict(value = "blogs", allEntries = true)
            }
    )
    public BlogPostResponse createOrUpdatePost(BlogPostRequest request) {
        BlogPost blogPost;
        if (!StringUtils.hasText(request.getId())) {
            BlogCategory blogCategory = null;
            if (request.getBlogCategoryId() != null) {
                blogCategory = blogCategoryRepo.findById(request.getBlogCategoryId())
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Blog category not found")
                        );
            }
            blogPost = BlogPost.builder()
                    .blogCategory(blogCategory)
                    .title(request.getTitle())
                    .content(request.getContent())
                    .home(request.isHome())
                    .files(FileMapper.INSTANCE.toEntities(request.getFiles()))
                    .build();
        } else {
            blogPost = getById(request.getId());
            blogPost = BlogPostMapper.INSTANCE.partialUpdate(request, blogPost);
        }
        blogPost = blogPostRepo.save(blogPost);
        return BlogPostMapper.INSTANCE.toResponse(blogPost);
    }

    /**
     * Retrieves a paginated list of blog posts based on the provided parameters.
     *
     * @param pageNo     The page number to retrieve (0-indexed).
     * @param pageSize   The number of blog posts to include in each page.
     * @param sortBy     The attribute by which the blog posts should be sorted.
     * @param sortDir    The sorting direction, either "ASC" (ascending) or "DESC" (descending).
     * @param searchText The search searchText used to filter blog posts by title or content.
     * @return A {@link BlogPostPaginationResponse} containing the paginated list of blog posts.
     */
    @Override
    @Cacheable(value = "blogs", key = "T(java.util.Objects).hash(#pageNo, #pageSize, #sortBy, #sortDir, #searchText)")
    public BlogPostPaginationResponse getBlogs(int pageNo, int pageSize, String sortBy, String sortDir,
                                               String searchText) {
        log.info("Fetching blog posts");
        Sort sort = Sort.by(sortBy);
        sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Specification<BlogPost> spec = Specification.where(null);

        if (StringUtils.hasText(searchText)) {
            spec = spec.or(BlogPostSpecifications.searchBy(searchText));
        }

        spec = spec.and(BlogPostSpecifications.getAll());

        Page<BlogPost> menuPage = blogPostRepo.findAll(spec, pageable);
        var contents = BlogPostMapper.INSTANCE.toResponses(menuPage.getContent());

        return BlogPostPaginationResponse.builder()
                .contents(contents)
                .pageNo(menuPage.getNumber())
                .totalElements(menuPage.getTotalElements())
                .totalPages(menuPage.getTotalPages())
                .pageSize(menuPage.getSize())
                .last(menuPage.isLast())
                .build();
    }

    /**
     * Retrieves detailed information for a specific blog post identified by the given ID.
     *
     * @param id The unique identifier of the blog post.
     * @return A {@link BlogPostResponse} containing detailed information about the blog post.
     * @throws ResourceNotFoundException If the specified blog post ID does not correspond to an existing blog post.
     */
    @Override
    public BlogPostResponse getDetail(String id) {
        var blogPost = blogPostRepo.getDetail(id).orElseThrow(
                () -> new ResourceNotFoundException("Blog post not found")
        );
        return BlogPostMapper.INSTANCE.toResponse(blogPost);
    }

    /**
     * Retrieves a list of blog posts limited by the constant BLOG_LIMITED.
     *
     * @return A list of {@link BlogPostResponse} containing limited blog posts.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Cacheable(value = "limitedblogs")
    @Override
    public List<BlogPostResponse> getLimitedBlogPosts() {
        log.info("Fetching limited blog posts");
        var blogPosts = blogPostRepo.findLimitedNumPostsByCreatedDateDesc();
        return BlogPostMapper.INSTANCE.toResponses(blogPosts);
    }

    /**
     * Deletes the blog post identified by the given ID.
     *
     * @param id The unique identifier of the blog post to delete.
     * @throws ResourceNotFoundException If the specified blog post ID does not correspond to an existing blog post.
     */
    @Transactional
    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "limitedblogs", allEntries = true),
                    @CacheEvict(value = "blogs", allEntries = true)
            }
    )
    public void deletePost(String id) {
        var blogPost = getById(id);
        blogPostRepo.delete(blogPost);
    }

    /**
     * Retrieves a blog post identified by the given ID.
     *
     * @param id The unique identifier of the blog post to retrieve.
     * @return The {@link BlogPost} identified by the given ID.
     * @throws ResourceNotFoundException If the specified blog post ID does not correspond to an existing blog post.
     */
    BlogPost getById(String id) {
        return blogPostRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Blog post not found")
        );
    }
}
