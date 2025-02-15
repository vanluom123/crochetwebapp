package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.crochet.constant.AppConstant;
import org.crochet.constant.MessageConstant;
import org.crochet.payload.request.BlogPostRequest;
import org.crochet.payload.request.Filter;
import org.crochet.payload.response.BlogPostResponse;
import org.crochet.payload.response.PaginationResponse;
import org.crochet.payload.response.ResponseData;
import org.crochet.service.BlogPostService;
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
@RequestMapping("/api/v1/blogs")
public class BlogController {
    private final BlogPostService blogPostService;

    public BlogController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    @Operation(summary = "Create or update a blog post")
    @ApiResponse(responseCode = "201", description = "Blog post created or updated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseData<String> createOrUpdatePost(@RequestBody BlogPostRequest request) {
        blogPostService.createOrUpdatePost(request);
        return ResponseData.<String>builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message(MessageConstant.MSG_CREATE_OR_UPDATE_SUCCESS)
                .build();
    }

    @Operation(summary = "Get paginated list of blog posts")
    @ApiResponse(responseCode = "200", description = "List of blog posts",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PaginationResponse.class)))
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    public ResponseData<PaginationResponse<BlogPostResponse>> getBlogs(
            @Parameter(description = "Page number (default: 0)")
            @RequestParam(value = "offset", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER,
                    required = false) int offset,
            @Parameter(description = "Page size (default: 48)")
            @RequestParam(value = "limit", defaultValue = AppConstant.DEFAULT_PAGE_SIZE,
                    required = false) int limit,
            @Parameter(description = "Sort by field (default: createdDate)")
            @RequestParam(value = "sortBy", defaultValue = AppConstant.DEFAULT_SORT_BY, required = false) String sortBy,
            @Parameter(description = "Sort direction (default: DESC)")
            @RequestParam(value = "sortDir", defaultValue = AppConstant.DEFAULT_SORT_DIRECTION,
                    required = false) String sortDir,
            @Parameter(description = "The list of filters")
            @RequestBody(required = false) Filter[] filters) {
        var response = blogPostService.getBlogs(offset, limit, sortBy, sortDir, filters);
        return ResponseData.<PaginationResponse<BlogPostResponse>>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SUCCESS)
                .data(response)
                .build();
    }

    @Operation(summary = "Get details of a post")
    @ApiResponse(responseCode = "200", description = "Details of a post",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BlogPostResponse.class)))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ResponseData<BlogPostResponse> getDetail(@PathVariable("id") String id) {
        var response = blogPostService.getDetail(id);
        return ResponseData.<BlogPostResponse>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SUCCESS)
                .data(response)
                .build();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a blog post")
    @ApiResponse(responseCode = "204", description = "A post deleted successfully")
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseData<String> deletePost(@RequestParam("id") String id) {
        blogPostService.deletePost(id);
        return ResponseData.<String>builder()
                .success(true)
                .code(HttpStatus.NO_CONTENT.value())
                .message(MessageConstant.MSG_DELETE_SUCCESS)
                .build();
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get blog ids")
    @ApiResponse(responseCode = "200", description = "List of blog ids",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = List.class)))
    @GetMapping("/ids")
    public ResponseData<List<String>> getBlogIds(
            @Parameter(description = "Page number (default: 0)")
            @RequestParam(value = "offset", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER,
                    required = false) int offset,
            @Parameter(description = "Limit (default: 48)")
            @RequestParam(value = "limit", defaultValue = AppConstant.DEFAULT_PAGE_SIZE, required = false) int limit) {
        var response = blogPostService.getBlogIds(offset, limit);
        return ResponseData.<List<String>>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SUCCESS)
                .data(response)
                .build();
    }
}
