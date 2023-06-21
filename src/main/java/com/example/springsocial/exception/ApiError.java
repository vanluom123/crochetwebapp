package com.example.springsocial.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
@AllArgsConstructor
public class ApiError {
  private String message;
  private HttpStatus statusCode;
  private Throwable rootCause;
}
