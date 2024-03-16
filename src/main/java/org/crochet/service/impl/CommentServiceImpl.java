package org.crochet.service.impl;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.CommentMapper;
import org.crochet.model.Comment;
import org.crochet.model.User;
import org.crochet.payload.request.CommentRequest;
import org.crochet.payload.response.CommentResponse;
import org.crochet.repository.BlogPostRepository;
import org.crochet.repository.CommentRepository;
import org.crochet.repository.UserRepository;
import org.crochet.security.UserPrincipal;
import org.crochet.service.CommentService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.crochet.constant.MessageCode.*;
import static org.crochet.constant.MessageConstant.*;

/**
 * CommentServiceImpl class
 */
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepo;

    private final UserRepository userRepo;

    private final BlogPostRepository blogPostRepo;

    /**
     * Constructs a new {@code CommentServiceImpl} with the specified repositories.
     *
     * @param commentRepo  The repository for handling comments.
     * @param userRepo     The repository for handling users.
     * @param blogPostRepo The repository for handling blog posts.
     */
    public CommentServiceImpl(CommentRepository commentRepo,
                              UserRepository userRepo,
                              BlogPostRepository blogPostRepo) {
        this.commentRepo = commentRepo;
        this.userRepo = userRepo;
        this.blogPostRepo = blogPostRepo;
    }

    /**
     * Creates a new comment or updates an existing one based on the provided {@link CommentRequest}.
     * If the request contains an ID, it updates the existing comment with the corresponding ID.
     * If the request does not contain an ID, it creates a new comment.
     *
     * @param request The {@link CommentRequest} containing information for creating or updating the comment.
     * @return
     * @throws ResourceNotFoundException If an existing comment is to be updated, and the specified ID is not found.
     */
    @Transactional
    @Override
    public CommentResponse createOrUpdate(CommentRequest request) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ResourceNotFoundException(PLEASE_LOGIN_TO_COMMENT_MESSAGE, PLEASE_LOGIN_TO_COMMENT_CODE);
        }
        User user = userRepo.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE, USER_NOT_FOUND_CODE));

        var blog = blogPostRepo.findById(UUID.fromString(request.getBlogPostId()))
                .orElseThrow(() -> new ResourceNotFoundException(BLOG_NOT_FOUND_MESSAGE, BLOG_NOT_FOUND_CODE));

        var id = request.getId();
        Comment comment;
        if (id == null) {
            comment = new Comment();
            comment.setBlogPost(blog);
            comment.setUser(user);
        } else {
            comment = findOne(id);
        }
        comment.setContent(request.getContent());
        comment.setCreatedDate(LocalDateTime.now());
        comment = commentRepo.save(comment);
        return CommentMapper.INSTANCE.toResponse(comment);
    }

    private Comment findOne(String id) {
        return commentRepo.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException(COMMENT_NOT_FOUND_MESSAGE, COMMENT_NOT_FOUND_CODE));
    }
}
