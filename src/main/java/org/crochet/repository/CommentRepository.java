package org.crochet.repository;

import java.util.List;

import org.crochet.model.Comment;
import org.crochet.payload.response.CommentResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    @Query("""
            SELECT new org.crochet.payload.response.CommentResponse(c.id, c.content, c.createdDate)
            FROM Comment c
            LEFT JOIN User u ON c.user.id = u.id
            WHERE u.id = :userId
            ORDER BY c.createdDate DESC
            LIMIT 5
            """)
    List<CommentResponse> getRecentCommentsByUserId(@Param("userId") String userId);
}