package org.crochet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler({BadRequestException.class})
  public ResponseEntity<ApiError> handleBadRequestException(BadRequestException ex) {
    ApiError err = ApiError.create()
        .setMessage(ex.getMessage())
        .setStatusCode(HttpStatus.BAD_REQUEST);
    return ResponseEntity.ok(err);
  }

  @ExceptionHandler({OAuth2AuthenticationProcessingException.class})
  public ResponseEntity<ApiError> handleOAuth2AuthenticationProcessingException(OAuth2AuthenticationProcessingException ex) {
    ApiError err = ApiError.create()
        .setMessage(ex.getMessage())
        .setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
    return ResponseEntity.ok(err);
  }

  @ExceptionHandler({ResourceNotFoundException.class})
  public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex) {
    ApiError err = ApiError.create()
        .setMessage(ex.getMessage())
        .setStatusCode(HttpStatus.NOT_FOUND);
    return ResponseEntity.ok(err);
  }

  @ExceptionHandler({EmailVerificationException.class})
  public ResponseEntity<ApiError> handleEmailVerificationException(EmailVerificationException ex) {
    ApiError err = ApiError.create()
        .setMessage(ex.getMessage())
        .setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
    return ResponseEntity.ok(err);
  }

  @ExceptionHandler({TokenException.class})
  public ResponseEntity<ApiError> handleTokenException(TokenException ex) {
    ApiError err = ApiError.create()
        .setMessage(ex.getMessage())
        .setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
    return ResponseEntity.ok(err);
  }

  @ExceptionHandler({UsernameNotFoundException.class})
  public ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException ex) {
    ApiError err = ApiError.create()
        .setMessage(ex.getMessage())
        .setStatusCode(HttpStatus.NOT_FOUND);
    return ResponseEntity.ok(err);
  }
}
