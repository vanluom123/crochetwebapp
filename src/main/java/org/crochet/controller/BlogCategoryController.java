package org.crochet.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.crochet.constant.MessageConstant;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.crochet.constant.AppConstant.SUCCESS;

@RestController
@RequestMapping("/api/v1/blog-categories")
@RequiredArgsConstructor
public class BlogCategoryController {
    private final BlogCategoryService blogCategoryService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/createOrUpdate")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseData<String> createOrUpdate(@RequestBody BlogCategoryRequest request) {
        blogCategoryService.createOrUpdate(request);
        return new ResponseData<>(
                true,
                HttpStatus.OK.value(),
                MessageConstant.MSG_CREATE_OR_UPDATE_SUCCESS,
                null,
                null);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseData<String> delete(@RequestParam("id") String id) {
        blogCategoryService.delete(id);
        return new ResponseData<>(
                true,
                HttpStatus.OK.value(),
                MessageConstant.MSG_DELETE_SUCCESS,
                null,
                null);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ResponseData<BlogCategoryResponse> getDetail(@PathVariable String id) {
        var res = blogCategoryService.getDetail(id);
        return new ResponseData<>(
                true,
                HttpStatus.OK.value(),
                SUCCESS,
                res,
                null);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<List<BlogCategoryResponse>> getAll() {
        var res = blogCategoryService.getAll();
        return new ResponseData<>(
                true,
                HttpStatus.OK.value(),
                SUCCESS,
                res,
                null);
    }
}
