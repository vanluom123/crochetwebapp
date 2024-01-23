package org.crochet.controller;

import org.crochet.service.contact.BlogStorageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/blog-storage")
public class BlogStorageController {
    private final BlogStorageService blogStorageService;

    public BlogStorageController(BlogStorageService blogStorageService) {
        this.blogStorageService = blogStorageService;
    }

    @PostMapping(value = "/upload", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<List<String>> uploadMultipleFile(List<MultipartFile> file) {
        try {
            return ResponseEntity.ok(blogStorageService.uploadMultipleFile(file));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
