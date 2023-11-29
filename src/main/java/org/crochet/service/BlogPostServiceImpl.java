package org.crochet.service;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.BlogPostMapper;
import org.crochet.model.BlogPost;
import org.crochet.repository.BlogPostRepository;
import org.crochet.repository.BlogPostSpecifications;
import org.crochet.request.BlogPostRequest;
import org.crochet.response.BlogPostPaginationResponse;
import org.crochet.response.BlogPostResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BlogPostServiceImpl implements BlogPostService {

    @Autowired
    private BlogPostRepository blogPostRepo;

    @Autowired
    private BlogPostMapper mapper;

    @Transactional
    @Override
    public void createOrUpdatePost(BlogPostRequest request) {
        var blogPost = blogPostRepo.findById(request.getId()).orElse(null);
        if (blogPost == null) {
            // create new a post
            blogPost = BlogPost.builder()
                    .id(request.getId())
                    .title(request.getTitle())
                    .content(request.getContent())
                    .imageUrl(request.getImageUrl())
                    .creationDate(LocalDateTime.now())
                    .build();
        } else {
            // update the post
            blogPost.setTitle(request.getTitle());
            blogPost.setContent(request.getContent());
            blogPost.setImageUrl(request.getImageUrl());
        }

        blogPostRepo.save(blogPost);
    }

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
        var contents = mapper.toResponses(menuPage.getContent());

        return BlogPostPaginationResponse.builder()
                .contents(contents)
                .pageNo(menuPage.getNumber())
                .totalElements(menuPage.getTotalElements())
                .totalPages(menuPage.getTotalPages())
                .pageSize(menuPage.getSize())
                .last(menuPage.isLast())
                .build();
    }

    @Override
    public BlogPostResponse getDetail(long id) {
        var blogPost = blogPostRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found"));
        return mapper.toResponse(blogPost);
    }
}
