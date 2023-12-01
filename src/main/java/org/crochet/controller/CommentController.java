package org.crochet.controller;

import org.crochet.request.CommentRequest;
import org.crochet.service.contact.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createComment(@RequestBody CommentRequest request) {
        commentService.createOrUpdate(request);
        return ResponseEntity.ok("Create comment successfully");
    }
}
