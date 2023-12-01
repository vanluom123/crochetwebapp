package org.crochet.controller;

import org.crochet.constant.AppConstant;
import org.crochet.request.BlogPostRequest;
import org.crochet.response.BlogPostPaginationResponse;
import org.crochet.service.contact.BlogPostService;
import org.crochet.service.contact.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/blog")
public class BlogController {
    private final BlogPostService blogPostService;

    private final FirebaseService firebaseService;

    public BlogController(BlogPostService blogPostService, FirebaseService firebaseService) {
        this.blogPostService = blogPostService;
        this.firebaseService = firebaseService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createOrUpdatePost(@RequestParam("filePath") String filePath,
                                                     @RequestBody BlogPostRequest request) {
        var img = firebaseService.getFile(filePath);
        var base64 = Base64.getEncoder().encodeToString(img);
        request.setImageUrl(base64);
        blogPostService.createOrUpdatePost(request);
        return ResponseEntity.ok("Create post successfully");
    }

    @GetMapping("/pagination")
    public ResponseEntity<BlogPostPaginationResponse> getBlogs(
            @RequestParam(value = "pageNo", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstant.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstant.DEFAULT_SORT_DIRECTION,
                    required = false) String sortDir,
            @RequestParam(value = "text", required = false) String text) {
        var response = blogPostService.getBlogs(pageNo, pageSize, sortBy, sortDir, text);
        return ResponseEntity.ok(response);
    }
}
