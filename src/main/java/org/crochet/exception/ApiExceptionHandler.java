package org.crochet.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiError> handleException(Exception ex) {
        ApiError error = ApiError.builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        log.error(ex.getMessage());
        return ResponseEntity.ok(error);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex) {
        ApiError error = ApiError.builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.FORBIDDEN)
                .build();
        log.error(ex.getMessage());
        return ResponseEntity.ok(error);
    }

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ApiError> handleBadRequestException(BadRequestException ex) {
        ApiError err = ApiError.builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST)
                .build();
        log.error(ex.getMessage());
        return ResponseEntity.ok(err);
    }

    @ExceptionHandler({OAuth2AuthenticationProcessingException.class})
    public ResponseEntity<ApiError> handleOAuth2AuthenticationProcessingException(OAuth2AuthenticationProcessingException ex) {
        ApiError err = ApiError.builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        log.error(ex.getMessage());
        return ResponseEntity.ok(err);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiError err = ApiError.builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.NOT_FOUND)
                .build();
        log.error(ex.getMessage());
        return ResponseEntity.ok(err);
    }

    @ExceptionHandler({EmailVerificationException.class})
    public ResponseEntity<ApiError> handleEmailVerificationException(EmailVerificationException ex) {
        ApiError err = ApiError.builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        log.error(ex.getMessage());
        return ResponseEntity.ok(err);
    }

    @ExceptionHandler({TokenException.class})
    public ResponseEntity<ApiError> handleTokenException(TokenException ex) {
        ApiError err = ApiError.builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        log.error(ex.getMessage());
        return ResponseEntity.ok(err);
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ApiError err = ApiError.builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.NOT_FOUND)
                .build();
        log.error(ex.getMessage());
        return ResponseEntity.ok(err);
    }

    @ExceptionHandler({CloudStorageException.class})
    public ResponseEntity<ApiError> handleCloudStorageException(CloudStorageException ex) {
        ApiError err = ApiError.builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        log.error(ex.getMessage());
        return ResponseEntity.ok(err);
    }
}
