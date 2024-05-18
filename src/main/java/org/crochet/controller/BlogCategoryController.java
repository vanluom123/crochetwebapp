package org.crochet.controller;

import lombok.RequiredArgsConstructor;
import org.crochet.payload.request.BlogCategoryRequest;
import org.crochet.payload.response.BlogCategoryResponse;
import org.crochet.service.BlogCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/blog-categories")
@RequiredArgsConstructor
public class BlogCategoryController {
    private final BlogCategoryService blogCategoryService;

    @PostMapping("/create")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public BlogCategoryResponse createBlogCategory(@RequestBody BlogCategoryRequest request) {
        return blogCategoryService.createBlogCategory(request);
    }

    @PutMapping("/update")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BlogCategoryResponse updateBlogCategory(@RequestBody BlogCategoryRequest request) {
        return blogCategoryService.updateBlogCategory(request);
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBlogCategory(String id) {
        blogCategoryService.deleteBlogCategory(id);
    }

    @GetMapping("/detail")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BlogCategoryResponse getDetail(String id) {
        return blogCategoryService.getBlogCategory(id);
    }

    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<BlogCategoryResponse> getAll() {
        return blogCategoryService.getBlogCategories();
    }
}
