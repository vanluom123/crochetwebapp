package org.crochet.controller;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.crochet.constant.AppConstant;
import org.crochet.enums.ResultCode;
import org.crochet.model.BlogPost;
import org.crochet.payload.request.BlogPostRequest;
import org.crochet.payload.response.BlogPostResponse;
import org.crochet.payload.response.PaginationResponse;
import org.crochet.payload.response.ResponseData;
import org.crochet.service.BlogPostService;
import org.crochet.util.ResponseUtil;
import org.springframework.data.jpa.domain.Specification;
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
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseData<String> createOrUpdatePost(@RequestBody BlogPostRequest request) {
        blogPostService.createOrUpdatePost(request);
        return ResponseUtil.success(ResultCode.MSG_CREATE_OR_UPDATE_SUCCESS.message());
    }

    @Operation(summary = "Get paginated list of blog posts")
    @ApiResponse(responseCode = "200", description = "List of blog posts",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PaginationResponse.class)))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ResponseData<PaginationResponse<BlogPostResponse>> getBlogs(
            @Parameter(description = "Page number (default: 0)")
            @RequestParam(value = "pageNo", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER,
                    required = false) int pageNo,
            @Parameter(description = "Page size (default: 48)")
            @RequestParam(value = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE,
                    required = false) int pageSize,
            @Parameter(description = "Sort by field (default: createdDate)")
            @RequestParam(value = "sortBy", defaultValue = AppConstant.DEFAULT_SORT_BY, required = false) String sortBy,
            @Parameter(description = "Sort direction (default: DESC)")
            @RequestParam(value = "sortDir", defaultValue = AppConstant.DEFAULT_SORT_DIRECTION,
                    required = false) String sortDir,
            @Filter Specification<BlogPost> spec) {
        var response = blogPostService.getBlogs(pageNo, pageSize, sortBy, sortDir, spec);
        return ResponseUtil.success(response);
    }

    @Operation(summary = "Get details of a post")
    @ApiResponse(responseCode = "200", description = "Details of a post",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BlogPostResponse.class)))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ResponseData<BlogPostResponse> getDetail(@PathVariable("id") String id) {
        var response = blogPostService.getDetail(id);
        return ResponseUtil.success(response);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete a blog post")
    @ApiResponse(responseCode = "200", description = "A post deleted successfully")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseData<String> deletePost(@PathVariable("id") String id) {
        blogPostService.deletePost(id);
        return ResponseUtil.success(ResultCode.MSG_DELETE_SUCCESS.message());
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get blog ids")
    @ApiResponse(responseCode = "200", description = "List of blog ids",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = List.class)))
    @GetMapping("/all-ids")
    public ResponseData<List<String>> getBlogIds(
            @Parameter(description = "Page number (default: 0)")
            @RequestParam(value = "pageNo", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER,
                    required = false) int pageNo,
            @Parameter(description = "Limit (default: 48)")
            @RequestParam(value = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE, required = false) int pageSize) {
        var response = blogPostService.getBlogIds(pageNo, pageSize);
        return ResponseUtil.success(response);
    }
}
