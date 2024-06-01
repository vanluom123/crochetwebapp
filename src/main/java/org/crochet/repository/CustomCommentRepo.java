package org.crochet.repository;

import org.crochet.model.Comment;
import org.springframework.stereotype.Repository;

@Repository
public class CustomCommentRepo extends BaseRepositoryImpl<Comment, String> {
}
