package com.example.springsocial.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler({BadRequestException.class})
  public ResponseEntity<ApiError> handleBadRequestException(BadRequestException ex) {
    ApiError err = ApiError.builder()
        .message(ex.getMessage())
        .statusCode(HttpStatus.BAD_REQUEST)
        .rootCause(ex.getCause())
        .build();
    return ResponseEntity.ok(err);
  }

  @ExceptionHandler({OAuth2AuthenticationProcessingException.class})
  public ResponseEntity<ApiError> handleOAuth2AuthenticationProcessingException(OAuth2AuthenticationProcessingException ex) {
    ApiError err = ApiError.builder()
        .message(ex.getMessage())
        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
        .rootCause(ex.getCause())
        .build();
    return ResponseEntity.ok(err);
  }

  @ExceptionHandler({ResourceNotFoundException.class})
  public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex) {
    ApiError err = ApiError.builder()
        .message(ex.getMessage())
        .statusCode(HttpStatus.NOT_FOUND)
        .rootCause(ex.getCause())
        .build();
    return ResponseEntity.ok(err);
  }
}
