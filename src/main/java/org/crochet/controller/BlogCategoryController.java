package org.crochet.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.crochet.payload.request.BlogCategoryRequest;
import org.crochet.payload.response.BlogCategoryResponse;
import org.crochet.payload.response.ResponseData;
import org.crochet.service.BlogCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/createOrUpdate")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseData<String> createOrUpdate(@RequestBody BlogCategoryRequest request) {
        blogCategoryService.createOrUpdate(request);
        return ResponseData.<String>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Create or update success")
                .data("Create or update success")
                .build();
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = "BearerAuth")
    public void deleteBlogCategory(@PathVariable String id) {
        blogCategoryService.delete(id);
    }

    @GetMapping("/detail/{id}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BlogCategoryResponse getDetail(@PathVariable String id) {
        return blogCategoryService.getDetail(id);
    }

    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<BlogCategoryResponse> getAll() {
        return blogCategoryService.getAll();
    }
}
