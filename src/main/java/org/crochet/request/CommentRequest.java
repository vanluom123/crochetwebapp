package org.crochet.request;

import lombok.Data;

@Data
public class CommentRequest {
  private long id;
  private long blogPostId;
  private String content;
}
