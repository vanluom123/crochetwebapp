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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;

  private final UserRepository userRepository;

  private final BlogPostRepository blogPostRepository;

  public CommentServiceImpl(CommentRepository commentRepository,
                            UserRepository userRepository,
                            BlogPostRepository blogPostRepository) {
    this.commentRepository = commentRepository;
    this.userRepository = userRepository;
    this.blogPostRepository = blogPostRepository;
  }

  @Transactional
  @Override
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
