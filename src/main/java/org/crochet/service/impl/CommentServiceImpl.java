package org.crochet.service.impl;

import lombok.RequiredArgsConstructor;
import org.crochet.enums.ResultCode;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.CommentMapper;
import org.crochet.model.Comment;
import org.crochet.model.User;
import org.crochet.payload.request.CommentRequest;
import org.crochet.payload.response.CommentResponse;
import org.crochet.repository.BlogPostRepository;
import org.crochet.repository.CommentRepository;
import org.crochet.service.CommentService;
import org.crochet.util.ObjectUtils;
import org.crochet.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


/**
 * CommentServiceImpl class
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepo;
    private final BlogPostRepository blogPostRepo;

    /**
     * Creates a new comment or updates an existing one based on the provided {@link CommentRequest}.
     * If the request contains an ID, it updates the existing comment with the corresponding ID.
     * If the request does not contain an ID, it creates a new comment.
     *
     * @param request The {@link CommentRequest} containing information for creating or updating the comment.
     * @return The {@link CommentResponse} containing information about the created or updated comment.
     * @throws ResourceNotFoundException If an existing comment is to be updated, and the specified ID is not found.
     */
    @Transactional
    @Override
    public CommentResponse createOrUpdate(CommentRequest request) {
        User user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new ResourceNotFoundException(
                    ResultCode.MSG_USER_LOGIN_REQUIRED.message(),
                    ResultCode.MSG_USER_LOGIN_REQUIRED.code()
            );
        }

        var blog = blogPostRepo.findById(request.getBlogPostId()).orElseThrow(
                () -> new ResourceNotFoundException(
                        ResultCode.MSG_BLOG_NOT_FOUND.message(),
                        ResultCode.MSG_BLOG_NOT_FOUND.code()
                )
        );

        var id = request.getId();
        Comment comment;
        if (!ObjectUtils.hasText(id)) {
            comment = Comment.builder()
                    .blogPost(blog)
                    .user(user)
                    .build();
        } else {
            comment = commentRepo.findById(id).orElseThrow(
                    () -> new ResourceNotFoundException(
                            ResultCode.MSG_COMMENT_NOT_FOUND.message(),
                            ResultCode.MSG_COMMENT_NOT_FOUND.code()
                    )
            );
        }
        comment.setContent(request.getContent());
        comment.setCreatedDate(LocalDateTime.now());
        comment = commentRepo.save(comment);
        return CommentMapper.INSTANCE.toResponse(comment);
    }
}
