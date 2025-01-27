package org.crochet.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.crochet.constant.MessageConstant;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.BlogPostMapper;
import org.crochet.mapper.FileMapper;
import org.crochet.model.BlogCategory;
import org.crochet.model.BlogPost;
import org.crochet.model.Settings;
import org.crochet.payload.request.BlogPostRequest;
import org.crochet.payload.request.Filter;
import org.crochet.payload.response.BlogPostPaginationResponse;
import org.crochet.payload.response.BlogPostResponse;
import org.crochet.repository.BlogCategoryRepo;
import org.crochet.repository.BlogPostRepository;
import org.crochet.repository.GenericFilter;
import org.crochet.service.BlogPostService;
import org.crochet.util.ImageUtils;
import org.crochet.util.SettingsUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

import static org.crochet.constant.MessageCodeConstant.MAP_CODE;

/**
 * BlogPostServiceImpl class
 */
@Slf4j
@Service
public class BlogPostServiceImpl implements BlogPostService {
    private final BlogPostRepository blogPostRepo;
    private final BlogCategoryRepo blogCategoryRepo;
    private final SettingsUtil settingsUtil;

    public BlogPostServiceImpl(BlogPostRepository blogPostRepo,
                               BlogCategoryRepo blogCategoryRepo,
                               SettingsUtil settingsUtil) {
        this.blogPostRepo = blogPostRepo;
        this.blogCategoryRepo = blogCategoryRepo;
        this.settingsUtil = settingsUtil;
    }

    /**
     * Creates a new blog post or updates an existing one based on the provided
     * {@link BlogPostRequest}.
     * If the request contains an ID, it updates the existing blog post with the
     * corresponding ID.
     * If the request does not contain an ID, it creates a new blog post.
     *
     * @param request The {@link BlogPostRequest} containing information for
     *                creating or updating the blog post.
     */
    @Transactional
    @Override
    public void createOrUpdatePost(BlogPostRequest request) {
        BlogPost blogPost;
        var images = ImageUtils.sortFiles(request.getFiles());

        if (!StringUtils.hasText(request.getId())) {
            BlogCategory blogCategory = null;
            if (request.getBlogCategoryId() != null) {
                blogCategory = blogCategoryRepo.findById(request.getBlogCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.MSG_BLOG_CATEGORY_NOT_FOUND,
                                MAP_CODE.get(MessageConstant.MSG_BLOG_CATEGORY_NOT_FOUND)));
            }
            blogPost = BlogPost.builder()
                    .blogCategory(blogCategory)
                    .title(request.getTitle())
                    .content(request.getContent())
                    .home(request.isHome())
                    .files(FileMapper.INSTANCE.toEntities(images))
                    .build();
        } else {
            blogPost = getById(request.getId());
            blogPost = blogPost.toBuilder()
                    .title(request.getTitle())
                    .content(request.getContent())
                    .home(request.isHome())
                    .files(FileMapper.INSTANCE.toEntities(images))
                    .build();
        }
        blogPostRepo.save(blogPost);
    }

    /**
     * Retrieves a paginated list of blog posts based on the provided parameters.
     *
     * @param pageNo   The page number to retrieve (0-indexed).
     * @param pageSize The number of blog posts to include in each page.
     * @param sortBy   The attribute by which the blog posts should be sorted.
     * @param sortDir  The sorting direction, either "ASC" (ascending) or "DESC"
     *                 (descending).
     * @param filters  The list of filters.
     * @return A {@link BlogPostPaginationResponse} containing the paginated list of
     * blog posts.
     */
    @Override
    public BlogPostPaginationResponse getBlogs(int pageNo, int pageSize, String sortBy, String sortDir,
                                               Filter[] filters) {
        Specification<BlogPost> spec = Specification.where(null);
        if (filters != null && filters.length > 0) {
            GenericFilter<BlogPost> filter = GenericFilter.create(filters);
            spec = filter.build();
        }

        List<BlogPost> blogPosts = blogPostRepo.findAll(spec);
        var blogIds = blogPosts.stream()
                .map(BlogPost::getId)
                .toList();

        Sort.Direction dir = Sort.Direction.fromString(sortDir);
        Sort sort = Sort.by(dir, sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        var page = blogPostRepo.findBlogOnHomeWithIds(blogIds, pageable);

        return BlogPostPaginationResponse.builder()
                .contents(page.getContent())
                .pageNo(page.getNumber())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .pageSize(page.getSize())
                .last(page.isLast())
                .build();
    }

    /**
     * Get blog ids
     *
     * @param pageNo Page number
     * @param limit  Limit
     * @return List of blog ids
     */
    @Override
    public List<String> getBlogIds(int pageNo, int limit) {
        Pageable pageable = PageRequest.of(pageNo, limit);
        return blogPostRepo.getBlogIds(pageable);
    }

    /**
     * Retrieves detailed information for a specific blog post identified by the
     * given ID.
     *
     * @param id The unique identifier of the blog post.
     * @return A {@link BlogPostResponse} containing detailed information about the
     * blog post.
     * @throws ResourceNotFoundException If the specified blog post ID does not
     *                                   correspond to an existing blog post.
     */
    @Override
    public BlogPostResponse getDetail(String id) {
        var blogPost = blogPostRepo.getDetail(id).orElseThrow(
                () -> new ResourceNotFoundException(MessageConstant.MSG_BLOG_NOT_FOUND,
                        MAP_CODE.get(MessageConstant.MSG_BLOG_NOT_FOUND)));
        return BlogPostMapper.INSTANCE.toResponse(blogPost);
    }

    /**
     * Retrieves a list of blog posts limited by the constant BLOG_LIMITED.
     *
     * @return A list of {@link BlogPostResponse} containing limited blog posts.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    @Override
    public List<BlogPostResponse> getLimitedBlogPosts() {
        Map<String, Settings> settingsMap = settingsUtil.getSettingsMap();
        if (settingsMap.isEmpty()) {
            return List.of();
        }

        String direction = settingsMap.get("homepage.blog.direction").getValue();

        String orderBy = settingsMap.get("homepage.blog.orderBy").getValue();

        String limit = settingsMap.get("homepage.blog.limit").getValue();

        Sort sort = Sort.by(Sort.Direction.fromString(direction), orderBy);
        Pageable pageable = PageRequest.of(0, Integer.parseInt(limit), sort);

        return blogPostRepo.findLimitedNumPosts(pageable);
    }

    /**
     * Deletes the blog post identified by the given ID.
     *
     * @param id The unique identifier of the blog post to delete.
     * @throws ResourceNotFoundException If the specified blog post ID does not
     *                                   correspond to an existing blog post.
     */
    @Transactional
    @Override
    public void deletePost(String id) {
        var blogPost = getById(id);
        blogPostRepo.delete(blogPost);
    }

    /**
     * Retrieves a blog post identified by the given ID.
     *
     * @param id The unique identifier of the blog post to retrieve.
     * @return The {@link BlogPost} identified by the given ID.
     * @throws ResourceNotFoundException If the specified blog post ID does not
     *                                   correspond to an existing blog post.
     */
    private BlogPost getById(String id) {
        return blogPostRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(MessageConstant.MSG_BLOG_NOT_FOUND,
                        MAP_CODE.get(MessageConstant.MSG_BLOG_NOT_FOUND)));
    }
}
