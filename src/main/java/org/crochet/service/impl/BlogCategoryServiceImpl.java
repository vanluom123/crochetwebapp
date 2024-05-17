package org.crochet.service.impl;

import lombok.RequiredArgsConstructor;
import org.crochet.model.BlogCategory;
import org.crochet.payload.request.BlogCategoryRequest;
import org.crochet.payload.response.BlogCategoryResponse;
import org.crochet.repository.BlogCategoryRepo;
import org.crochet.service.BlogCategoryService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlogCategoryServiceImpl implements BlogCategoryService {
    private final BlogCategoryRepo blogCategoryRepo;

    public BlogCategoryResponse createBlogCategory(BlogCategoryRequest request) {
        BlogCategory blogCategory = new BlogCategory();
        blogCategory.setName(request.getName());
        blogCategory = blogCategoryRepo.save(blogCategory);
        return null;
    }
}
