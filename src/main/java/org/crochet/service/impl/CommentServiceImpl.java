package org.crochet.service.impl;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.CommentMapper;
import org.crochet.model.Comment;
import org.crochet.model.User;
import org.crochet.payload.request.CommentRequest;
import org.crochet.payload.response.CommentResponse;
import org.crochet.repository.CommentRepository;
import org.crochet.repository.CustomBlogRepo;
import org.crochet.repository.CustomCommentRepo;
import org.crochet.repository.CustomUserRepo;
import org.crochet.security.UserPrincipal;
import org.crochet.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.crochet.constant.MessageCodeConstant.MAP_CODE;
import static org.crochet.constant.MessageConstant.USER_NOT_FOUND_MESSAGE;

/**
 * CommentServiceImpl class
 */
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepo;
    private final CustomCommentRepo customCommentRepo;
    private final CustomUserRepo customUserRepo;
    private final CustomBlogRepo customBlogRepo;

    /**
     * Constructs a new {@code CommentServiceImpl} with the specified repositories.
     *
     * @param commentRepo  The repository for handling comments.
     * @param customCommentRepo The custom repository for handling comments.
     * @param customUserRepo The custom repository for handling users.
     * @param customBlogRepo The custom repository for handling blog posts.
     */
    public CommentServiceImpl(CommentRepository commentRepo,
                              CustomCommentRepo customCommentRepo,
                              CustomUserRepo customUserRepo,
                              CustomBlogRepo customBlogRepo) {
        this.commentRepo = commentRepo;
        this.customCommentRepo = customCommentRepo;
        this.customUserRepo = customUserRepo;
        this.customBlogRepo = customBlogRepo;
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
            throw new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE, MAP_CODE.get(USER_NOT_FOUND_MESSAGE));
        }
        User user = customUserRepo.findById(principal.getId());

        var blog = customBlogRepo.findById(request.getBlogPostId());

        var id = request.getId();
        Comment comment;
        if (id == null) {
            comment = new Comment();
            comment.setBlogPost(blog)
                    .setUser(user);
        } else {
            comment = customCommentRepo.findById(id);
        }
        comment.setContent(request.getContent())
                .setCreatedDate(LocalDateTime.now());
        comment = commentRepo.save(comment);
        return CommentMapper.INSTANCE.toResponse(comment);
    }
}
