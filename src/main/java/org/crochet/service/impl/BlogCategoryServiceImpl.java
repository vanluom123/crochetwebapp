package org.crochet.service.impl;

import lombok.RequiredArgsConstructor;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.BlogCategoryMapper;
import org.crochet.model.BlogCategory;
import org.crochet.payload.request.BlogCategoryRequest;
import org.crochet.payload.response.BlogCategoryResponse;
import org.crochet.repository.BlogCategoryRepo;
import org.crochet.service.BlogCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogCategoryServiceImpl implements BlogCategoryService {
    private final BlogCategoryRepo blogCategoryRepo;

    @Transactional
    @Override
    public BlogCategoryResponse createBlogCategory(BlogCategoryRequest request) {
        BlogCategory blogCategory = new BlogCategory();
        blogCategory.setName(request.getName());
        blogCategory = blogCategoryRepo.save(blogCategory);
        return BlogCategoryMapper.INSTANCE.toResponse(blogCategory);
    }

    @Transactional
    @Override
    public BlogCategoryResponse updateBlogCategory(BlogCategoryRequest request) {
        BlogCategory blogCategory = getBlogCategoryById(request.getId());
        blogCategory.setName(request.getName());
        blogCategory = blogCategoryRepo.save(blogCategory);
        return BlogCategoryMapper.INSTANCE.toResponse(blogCategory);
    }

    @Override
    public BlogCategoryResponse getBlogCategory(String id) {
        BlogCategory blogCategory = getBlogCategoryById(id);
        return BlogCategoryMapper.INSTANCE.toResponse(blogCategory);
    }

    @Override
    public List<BlogCategoryResponse> getBlogCategories() {
        List<BlogCategory> blogCategories = blogCategoryRepo.findAll();
        return BlogCategoryMapper.INSTANCE.toResponses(blogCategories);
    }

    @Transactional
    @Override
    public void deleteBlogCategory(String id) {
        BlogCategory blogCategory = getBlogCategoryById(id);
        blogCategoryRepo.delete(blogCategory);
    }

    private BlogCategory getBlogCategoryById(String id) {
        return blogCategoryRepo.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Blog category not found."));
    }
}
