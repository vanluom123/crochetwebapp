package org.crochet.exception;

import lombok.extern.slf4j.Slf4j;
import org.crochet.constant.MessageConstant;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.crochet.constant.MessageCodeConstant.MAP_CODE;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiError> handleException(Exception ex) {
        ApiError error = ApiError.builder()
                .message(ex.getMessage())
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .build();
        log.error(ex.getMessage());
        log.error(ex.toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException ex) {
        ApiError error = ApiError.builder()
                .message(ex.getMessage())
                .code(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .messageCode(MAP_CODE.get(MessageConstant.UNAUTHORIZED))
                .build();
        log.error(ex.getMessage());
        log.error(ex.toString());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ApiError> handleBadRequestException(BadRequestException ex) {
        ApiError err = ApiError.builder()
                .message(ex.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .messageCode(ex.getMessageCode())
                .build();
        log.error(ex.getMessage());
        log.error(ex.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler({OAuth2AuthenticationProcessingException.class})
    public ResponseEntity<ApiError> handleOAuth2AuthenticationProcessingException(
            OAuth2AuthenticationProcessingException ex) {
        ApiError err = ApiError.builder()
                .message(ex.getMessage())
                .code(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .messageCode(ex.getMessageCode())
                .build();
        log.error(ex.getMessage());
        log.error(ex.toString());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiError err = ApiError.builder()
                .message(ex.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .messageCode(ex.getMessageCode())
                .build();
        log.error(ex.getMessage());
        log.error(ex.toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @ExceptionHandler({EmailVerificationException.class})
    public ResponseEntity<ApiError> handleEmailVerificationException(EmailVerificationException ex) {
        return handleInternalError(ex, ex.getMessageCode());
    }

    @ExceptionHandler({TokenException.class})
    public ResponseEntity<ApiError> handleTokenException(TokenException ex) {
        ApiError error = ApiError.builder()
                .message(ex.getMessage())
                .code(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .messageCode(ex.getMessageCode())
                .build();
        log.error(ex.getMessage());
        log.error(ex.toString());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ApiError err = ApiError.builder()
                .message(ex.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .messageCode(MAP_CODE.get(MessageConstant.MSG_USER_NOT_FOUND))
                .build();
        log.error(ex.getMessage());
        log.error(ex.toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String message = ex.getMessage();
        if (ex.getCause() != null) {
            message = ex.getCause().getMessage();
        }
        ApiError err = ApiError.builder()
                .message(message)
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .messageCode(MAP_CODE.get(MessageConstant.DATA_INTEGRITY_VIOLATION))
                .build();
        log.error(ex.getMessage());
        log.error(ex.toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex) {
        ApiError err = ApiError.builder()
                .message(ex.getMessage())
                .code(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .messageCode(ex.getMessageCode())
                .build();
        log.error(ex.getMessage());
        log.error(ex.toString());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(err);
    }

    private ResponseEntity<ApiError> handleInternalError(RuntimeException ex, int messageCode) {
        ApiError err = ApiError.builder()
                .message(ex.getMessage())
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .messageCode(messageCode)
                .build();
        log.error(ex.getMessage());
        log.error(ex.toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
}
