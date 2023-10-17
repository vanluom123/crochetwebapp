package org.crochet.service;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.Comment;
import org.crochet.model.User;
import org.crochet.repository.BlogPostRepository;
import org.crochet.repository.CommentRepository;
import org.crochet.repository.UserRepository;
import org.crochet.request.CommentRequest;
import org.crochet.security.UserPrincipal;
import org.crochet.service.abstraction.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentServiceImpl implements CommentService {

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BlogPostRepository blogPostRepository;

  public void createOrUpdate(CommentRequest request) {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
    User user = userRepository.findById(principal.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

    var blog = blogPostRepository.findById(request.getBlogPostId()).orElseThrow(() -> new ResourceNotFoundException("Blog not found"));

    var comment = commentRepository.findById(request.getId()).orElse(null);
    if (comment == null) {
      comment = Comment.builder()
          .blogPost(blog)
          .user(user)
          .content(request.getContent())
          .createdDate(LocalDateTime.now())
          .build();
    } else {
      comment.setContent(request.getContent());
    }

    comment = commentRepository.save(comment);
  }
}
