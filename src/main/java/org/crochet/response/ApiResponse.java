package org.crochet.response;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class ApiResponse {
  private boolean success;
  private String message;
}
