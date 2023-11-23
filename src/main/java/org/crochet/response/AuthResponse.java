package org.crochet.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
  private String accessToken;

  @Builder.Default
  private String tokenType = "Bearer";

  public AuthResponse(String accessToken) {
    this.accessToken = accessToken;
  }
}
