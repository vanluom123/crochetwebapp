package com.example.springsocial.payload;

import lombok.Data;
import lombok.experimental.Accessors;

import java.net.URI;

@Data
@Accessors(chain = true)
public class RegisterResponse {
  private URI location;
  private ApiResponse apiResponse;
}
