package org.crochet.request;

import lombok.Data;

@Data
public class FreePatternRequest {
  private Long id;
  private String name;
  private String image;
  private String description;
}
