package org.crochet.response;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class FreePatternResponse {
  private long id;
  private String name;
  private String image;
  private String description;
}
