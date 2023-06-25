package com.example.springsocial.payload;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthResponse {
  private String accessToken;
  private String tokenType = "Bearer";

  public AuthResponse(String accessToken) {
    this.accessToken = accessToken;
  }
}
