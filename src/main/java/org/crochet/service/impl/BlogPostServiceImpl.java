package org.crochet.service.impl;

import com.turkraft.springfilter.converter.FilterSpecification;
import lombok.extern.slf4j.Slf4j;
import org.crochet.enums.ResultCode;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.BlogPostMapper;
import org.crochet.mapper.FileMapper;
import org.crochet.mapper.PaginationMapper;
import org.crochet.model.BlogCategory;
import org.crochet.model.BlogPost;
import org.crochet.model.Settings;
import org.crochet.payload.request.BlogPostRequest;
import org.crochet.payload.response.BlogPostResponse;
import org.crochet.payload.response.PaginationResponse;
import org.crochet.repository.BlogPostRepository;
import org.crochet.service.BlogCategoryService;
import org.crochet.service.BlogPostService;
import org.crochet.service.PermissionService;
import org.crochet.util.ImageUtils;
import org.crochet.util.ObjectUtils;
import org.crochet.util.SettingsUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * BlogPostServiceImpl class
 */
@Slf4j
@Service
public class BlogPostServiceImpl implements BlogPostService {
    private final BlogPostRepository blogPostRepo;
    private final BlogCategoryService blogCategoryService;
    private final SettingsUtil settingsUtil;
    private final PermissionService permissionService;

    public BlogPostServiceImpl(BlogPostRepository blogPostRepo,
                               BlogCategoryService blogCategoryService,
                               SettingsUtil settingsUtil,
                               PermissionService permissionService) {
        this.blogPostRepo = blogPostRepo;
        this.blogCategoryService = blogCategoryService;
        this.settingsUtil = settingsUtil;
        this.permissionService = permissionService;
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

        if (!ObjectUtils.hasText(request.getId())) {
            BlogCategory blogCategory = null;
            if (request.getBlogCategoryId() != null) {
                blogCategory = blogCategoryService.getById(request.getBlogCategoryId());
            }
            var images = ImageUtils.sortFiles(request.getFiles());
            blogPost = BlogPost.builder()
                    .blogCategory(blogCategory)
                    .title(request.getTitle())
                    .content(request.getContent())
                    .home(request.isHome())
                    .files(FileMapper.INSTANCE.toEntities(images))
                    .build();
        } else {
            blogPost = getById(request.getId());
            permissionService.checkUserPermission(blogPost, "update");
            blogPost = BlogPostMapper.INSTANCE.partialUpdate(request, blogPost);
        }
        blogPostRepo.save(blogPost);
    }

    /**
     * Retrieves a paginated list of blog posts based on the provided parameters.
     *
     * @param offset  The page number to retrieve (0-indexed).
     * @param limit   The number of blog posts to include in each page.
     * @param sortBy  The attribute by which the blog posts should be sorted.
     * @param sortDir The sorting direction, either "ASC" (ascending) or "DESC"
     *                (descending).
     * @param spec    The specification used to filter the blog posts.
     * @return A {@link org.crochet.payload.response.PaginationResponse} containing the paginated list of
     * blog posts.
     */
    @SuppressWarnings("ConstantValue")
    @Override
    public PaginationResponse<BlogPostResponse> getBlogs(int offset, int limit, String sortBy, String sortDir,
                                                         Specification<BlogPost> spec) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(offset, limit, sort);
        var filter = ((FilterSpecification<BlogPost>) spec).getFilter();
        Page<BlogPostResponse> page;
        if (filter != null && ObjectUtils.isNotEmpty(filter.getChildren())) {
            List<BlogPost> blogPosts = blogPostRepo.findAll(spec);
            var blogIds = blogPosts.stream()
                    .map(BlogPost::getId)
                    .toList();
            page = blogPostRepo.findPostWithIds(blogIds, pageable);
        } else {
            page = blogPostRepo.findPostWithPageable(pageable);
        }
        return PaginationMapper.toPagination(page);
    }

    /**
     * Get blog ids
     *
     * @param offset Page number
     * @param limit  Limit
     * @return List of blog ids
     */
    @Override
    public List<String> getBlogIds(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
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
                () -> new ResourceNotFoundException(
                        ResultCode.MSG_BLOG_NOT_FOUND.message(),
                        ResultCode.MSG_BLOG_NOT_FOUND.code()
                ));
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
        permissionService.checkUserPermission(blogPost, "delete");
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
    @Override
    public BlogPost getById(String id) {
        return blogPostRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(
                        ResultCode.MSG_BLOG_NOT_FOUND.message(),
                        ResultCode.MSG_BLOG_NOT_FOUND.code()
                ));
    }
}
