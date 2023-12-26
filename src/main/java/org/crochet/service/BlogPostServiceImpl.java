package org.crochet.service;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.BlogPostMapper;
import org.crochet.model.BlogPost;
import org.crochet.repository.BlogPostRepository;
import org.crochet.repository.BlogPostSpecifications;
import org.crochet.payload.request.BlogPostRequest;
import org.crochet.payload.response.BlogPostPaginationResponse;
import org.crochet.payload.response.BlogPostResponse;
import org.crochet.service.contact.BlogPostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.crochet.util.ConvertUtils.convertMultipartToString;

/**
 * BlogPostServiceImpl class
 */
@Service
public class BlogPostServiceImpl implements BlogPostService {
    private final BlogPostRepository blogPostRepo;

    /**
     * Constructs a new {@code BlogPostServiceImpl} with the specified BlogPost repository.
     *
     * @param blogPostRepo The repository for handling BlogPost-related operations.
     */
    public BlogPostServiceImpl(BlogPostRepository blogPostRepo) {
        this.blogPostRepo = blogPostRepo;
    }

    /**
     * Creates a new blog post or updates an existing one based on the provided {@link BlogPostRequest}.
     * If the request contains an ID, it updates the existing blog post with the corresponding ID.
     * If the request does not contain an ID, it creates a new blog post.
     *
     * @param request The {@link BlogPostRequest} containing information for creating or updating the blog post.
     * @param files
     * @return
     */
    @Transactional
    @Override
    public String createOrUpdatePost(BlogPostRequest request, List<MultipartFile> files) {
        var blogPost = (request.getId() == null) ? new BlogPost() : findOne(request.getId());
        blogPost.setTitle(request.getTitle());
        blogPost.setContent(request.getContent());
        blogPost.setCreationDate(LocalDateTime.now());
        blogPost.setFiles(convertMultipartToString(files));
        blogPostRepo.save(blogPost);
        return "Create blog successfully";
    }

    /**
     * Retrieves a paginated list of blog posts based on the provided parameters.
     *
     * @param pageNo   The page number to retrieve (0-indexed).
     * @param pageSize The number of blog posts to include in each page.
     * @param sortBy   The attribute by which the blog posts should be sorted.
     * @param sortDir  The sorting direction, either "ASC" (ascending) or "DESC" (descending).
     * @param text     The search text used to filter blog posts by title or content.
     * @return A {@link BlogPostPaginationResponse} containing the paginated list of blog posts.
     */
    @Override
    public BlogPostPaginationResponse getBlogs(int pageNo, int pageSize, String sortBy, String sortDir, String text) {
        // create Sort instance
        Sort sort = Sort.by(sortBy);
        sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? sort.ascending() : sort.descending();
        // create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Specification<BlogPost> spec = Specification.where(null);
        if (text != null && !text.isEmpty()) {
            spec = spec.and(BlogPostSpecifications.searchBy(text));
        }

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
        var blogPost = findOne(id);
        return BlogPostMapper.INSTANCE.toResponse(blogPost);
    }

    private BlogPost findOne(String id) {
        return blogPostRepo.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found"));
    }
}
