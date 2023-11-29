package org.crochet.service;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.Comment;
import org.crochet.model.User;
import org.crochet.repository.BlogPostRepository;
import org.crochet.repository.CommentRepository;
import org.crochet.repository.UserRepository;
import org.crochet.request.CommentRequest;
import org.crochet.security.UserPrincipal;
import org.crochet.service.contact.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BlogPostRepository blogPostRepo;

    @Transactional
    @Override
    public void createOrUpdate(CommentRequest request) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        User user = userRepo.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var blog = blogPostRepo.findById(UUID.fromString(request.getBlogPostId()))
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found"));

        var comment = commentRepo.findById(UUID.fromString(request.getId()))
                .orElse(null);
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

        commentRepo.save(comment);
    }
}
