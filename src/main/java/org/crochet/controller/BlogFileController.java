package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @Operation(summary = "Create blog files")
    @ApiResponse(responseCode = "200", description = "Blog files created successfully",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<List<BlogFileResponse>> create(
            @RequestPart("files") MultipartFile[] files,
            @Parameter(description = "ID of the blog to associate the files with")
            @RequestParam("blogId") String blogId) {
        var responses = blogFileService.createBlogFile(files, blogId);
        return ResponseEntity.ok(responses);
    }
}
