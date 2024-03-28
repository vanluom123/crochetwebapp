package org.crochet.service.impl;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.CommentMapper;
import org.crochet.model.Comment;
import org.crochet.model.User;
import org.crochet.payload.request.CommentRequest;
import org.crochet.payload.response.CommentResponse;
import org.crochet.properties.MessageCodeProperties;
import org.crochet.repository.BlogPostRepository;
import org.crochet.repository.CommentRepository;
import org.crochet.repository.UserRepository;
import org.crochet.security.UserPrincipal;
import org.crochet.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.crochet.constant.MessageConstant.*;

/**
 * CommentServiceImpl class
 */
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepo;
    private final UserRepository userRepo;
    private final BlogPostRepository blogPostRepo;
    private final MessageCodeProperties msgCodeProps;

    /**
     * Constructs a new {@code CommentServiceImpl} with the specified repositories.
     *
     * @param commentRepo  The repository for handling comments.
     * @param userRepo     The repository for handling users.
     * @param blogPostRepo The repository for handling blog posts.
     * @param msgCodeProps The properties for message codes.
     */
    public CommentServiceImpl(CommentRepository commentRepo,
                              UserRepository userRepo,
                              BlogPostRepository blogPostRepo,
                              MessageCodeProperties msgCodeProps) {
        this.commentRepo = commentRepo;
        this.userRepo = userRepo;
        this.blogPostRepo = blogPostRepo;
        this.msgCodeProps = msgCodeProps;
    }

    /**
     * Creates a new comment or updates an existing one based on the provided {@link CommentRequest}.
     * If the request contains an ID, it updates the existing comment with the corresponding ID.
     * If the request does not contain an ID, it creates a new comment.
     *
     * @param principal The {@link UserPrincipal} containing information about the authenticated user.
     * @param request   The {@link CommentRequest} containing information for creating or updating the comment.
     * @return The {@link CommentResponse} containing information about the created or updated comment.
     * @throws ResourceNotFoundException If an existing comment is to be updated, and the specified ID is not found.
     */
    @Transactional
    @Override
    public CommentResponse createOrUpdate(UserPrincipal principal, CommentRequest request) {
        if (principal == null) {
            throw new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE,
                    msgCodeProps.getCode("USER_NOT_FOUND_MESSAGE"));
        }
        User user = userRepo.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE,
                        msgCodeProps.getCode("USER_NOT_FOUND_MESSAGE")));

        var blog = blogPostRepo.findById(UUID.fromString(request.getBlogPostId()))
                .orElseThrow(() -> new ResourceNotFoundException(BLOG_NOT_FOUND_MESSAGE,
                        msgCodeProps.getCode("BLOG_NOT_FOUND_MESSAGE")));

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

    /**
     * Finds a comment by its ID.
     *
     * @param id The ID of the comment to find.
     * @return The comment with the specified ID.
     * @throws ResourceNotFoundException If the comment with the specified ID is not found.
     */
    private Comment findOne(String id) {
        return commentRepo.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException(COMMENT_NOT_FOUND_MESSAGE,
                        msgCodeProps.getCode("COMMENT_NOT_FOUND_MESSAGE")));
    }
}
