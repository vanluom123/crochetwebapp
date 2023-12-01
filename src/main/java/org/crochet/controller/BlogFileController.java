package org.crochet.controller;

import org.crochet.response.BlogFileResponse;
import org.crochet.service.contact.BlogFileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/blog-file")
public class BlogFileController {
    private final BlogFileService blogFileService;

    public BlogFileController(BlogFileService blogFileService) {
        this.blogFileService = blogFileService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BlogFileResponse>> create(@RequestPart MultipartFile[] files,
                                                         @RequestParam("blogId") String blogId) {
        var responses = blogFileService.createBlogFile(files, blogId);
        return ResponseEntity.ok(responses);
    }
}
