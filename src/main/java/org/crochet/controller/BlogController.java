package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.crochet.constant.AppConstant;
import org.crochet.payload.request.BlogPostRequest;
import org.crochet.payload.request.Filter;
import org.crochet.payload.response.BlogPostPaginationResponse;
import org.crochet.payload.response.BlogPostResponse;
import org.crochet.payload.response.ResponseData;
import org.crochet.service.BlogPostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/blog")
public class BlogController {
    private final BlogPostService blogPostService;

    public BlogController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Create or update a blog post")
    @ApiResponse(responseCode = "201", description = "Blog post created or updated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    @PostMapping(value = "/create")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseData<String> createOrUpdatePost(@RequestBody BlogPostRequest request) {
        blogPostService.createOrUpdatePost(request);
        return ResponseData.<String>builder()
                .success(true)
                .code(201)
                .message("Success")
                .data("Created or updated successfully")
                .build();
    }

    @Operation(summary = "Get paginated list of blog posts")
    @ApiResponse(responseCode = "200", description = "List of blog posts",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BlogPostPaginationResponse.class)))
    @PostMapping("/pagination")
    public ResponseEntity<BlogPostPaginationResponse> getBlogs(
            @Parameter(description = "Page number (default: 0)")
            @RequestParam(value = "pageNo", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER,
                    required = false) int pageNo,
            @Parameter(description = "Page size (default: 10)")
            @RequestParam(value = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE,
                    required = false) int pageSize,
            @Parameter(description = "Sort by field (default: id)")
            @RequestParam(value = "sortBy", defaultValue = AppConstant.DEFAULT_SORT_BY, required = false) String sortBy,
            @Parameter(description = "Sort direction (default: ASC)")
            @RequestParam(value = "sortDir", defaultValue = AppConstant.DEFAULT_SORT_DIRECTION,
                    required = false) String sortDir,
            @Parameter(description = "The list of filters")
            @RequestBody(required = false) Filter[] filters) {
        var response = blogPostService.getBlogs(pageNo, pageSize, sortBy, sortDir, filters);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get details of a blog post")
    @ApiResponse(responseCode = "200", description = "Details of a blog post",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BlogPostResponse.class)))
    @GetMapping("/detail")
    public ResponseEntity<BlogPostResponse> getDetail(@RequestParam String id) {
        var response = blogPostService.getDetail(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a blog post")
    @ApiResponse(responseCode = "204", description = "Blog post deleted successfully")
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<Void> deletePost(@RequestParam String id) {
        blogPostService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get blog ids")
    @ApiResponse(responseCode = "200", description = "List of blog ids",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = List.class)))
    @GetMapping("/ids")
    public ResponseEntity<List<String>> getBlogIds(
            @Parameter(description = "Page number (default: 0)")
            @RequestParam(value = "pageNo", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER,
                    required = false) int pageNo,
            @Parameter(description = "Limit (default: 10)")
            @RequestParam(value = "limit", defaultValue = AppConstant.DEFAULT_PAGE_SIZE, required = false) int limit) {
        var response = blogPostService.getBlogIds(pageNo, limit);
        return ResponseEntity.ok(response);
    }
}
