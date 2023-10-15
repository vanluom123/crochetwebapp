package org.crochet.controller;

import org.crochet.request.BlogPostRequest;
import org.crochet.service.abstraction.BlogPostService;
import org.crochet.service.abstraction.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/blog")
public class BlogController {

  @Autowired
  private BlogPostService blogPostService;

  @Autowired
  private FirebaseService firebaseService;

  @PostMapping("/create")
  public ResponseEntity<String> createOrUpdatePost(@RequestParam("filePath") String filePath,
                                                   @RequestBody BlogPostRequest request) {
    var img = firebaseService.getImage(filePath);
    var base64 = Base64.getEncoder().encodeToString(img);
    request.setImageUrl(base64);
    blogPostService.createOrUpdatePost(request);
    return ResponseEntity.ok("Create post successfully");
  }
}
