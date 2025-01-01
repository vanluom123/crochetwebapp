package org.crochet.service.impl;

import lombok.RequiredArgsConstructor;
import org.crochet.constant.MessageConstant;
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

import static org.crochet.constant.MessageCodeConstant.MAP_CODE;

@Service
@RequiredArgsConstructor
public class BlogCategoryServiceImpl implements BlogCategoryService {
    private final BlogCategoryRepo blogCategoryRepo;

    /**
     * Create a new blog category
     *
     * @param request the request object
     * @return the response object
     */
    @Transactional
    @Override
    public BlogCategoryResponse create(BlogCategoryRequest request) {
        BlogCategory blogCategory = new BlogCategory();
        blogCategory.setName(request.getName());
        blogCategory = blogCategoryRepo.save(blogCategory);
        return BlogCategoryMapper.INSTANCE.toResponse(blogCategory);
    }

    /**
     * Update a blog category
     *
     * @param request the request object
     * @return the response object
     */
    @Transactional
    @Override
    public BlogCategoryResponse update(BlogCategoryRequest request) {
        BlogCategory blogCategory = getById(request.getId());
        blogCategory.setName(request.getName());
        blogCategory = blogCategoryRepo.save(blogCategory);
        return BlogCategoryMapper.INSTANCE.toResponse(blogCategory);
    }

    /**
     * Get a blog category detail
     *
     * @param id the blog category id
     * @return the response object
     */
    @Override
    public BlogCategoryResponse getDetail(String id) {
        BlogCategory blogCategory = getById(id);
        return BlogCategoryMapper.INSTANCE.toResponse(blogCategory);
    }

    /**
     * Get all blog categories
     *
     * @return the list of blog categories
     */
    @Override
    public List<BlogCategoryResponse> getAll() {
        List<BlogCategory> blogCategories = blogCategoryRepo.findAll();
        return BlogCategoryMapper.INSTANCE.toResponses(blogCategories);
    }

    /**
     * Delete a blog category
     *
     * @param id the blog category id
     */
    @Transactional
    @Override
    public void delete(String id) {
        BlogCategory blogCategory = getById(id);
        blogCategoryRepo.delete(blogCategory);
    }

    /**
     * Get a blog category by id
     *
     * @param id the blog category id
     * @return the blog category
     */
    private BlogCategory getById(String id) {
        return blogCategoryRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(MessageConstant.MSG_BLOG_CATEGORY_NOT_FOUND,
                        MAP_CODE.get(MessageConstant.MSG_BLOG_CATEGORY_NOT_FOUND))
        );
    }
}
