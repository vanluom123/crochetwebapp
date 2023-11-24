package org.crochet.request;

import lombok.Data;

@Data
public class BlogPostRequest {
  private Long id;
  private String title;
  private String content;
  private String imageUrl;
}
