package org.crochet.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlogPostRequest {
  private Long id;
  private String title;
  private String content;
  private String imageUrl;
}
