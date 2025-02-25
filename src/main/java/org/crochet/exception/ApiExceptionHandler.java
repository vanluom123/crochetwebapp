package org.crochet.exception;

import lombok.extern.slf4j.Slf4j;
import org.crochet.enums.ResultCode;
import org.crochet.payload.response.ResponseData;
import org.crochet.util.ResponseUtil;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@ResponseBody
public class ApiExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public ResponseData<String> handleException(Exception ex) {
        var error = ResponseData.<String>builder()
                .success(false)
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .error(ex.getCause())
                .build();
        log.error(ex.getMessage());
        log.error(ex.toString());
        return error;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({AuthenticationException.class})
    public ResponseData<String> handleAuthenticationException(AuthenticationException ex) {
        var error = ResponseData.<String>builder()
                .success(false)
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(ex.getMessage())
                .error(ex.getCause())
                .build();
        log.error(ex.getMessage());
        log.error(ex.toString());
        return error;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadRequestException.class})
    public ResponseData<String> handleBadRequestException(BadRequestException ex) {
        var err = ResponseData.<String>builder()
                .success(false)
                .code(ex.getMessageCode())
                .message(ex.getMessage())
                .error(ex.getCause())
                .build();
        log.error(ex.getMessage());
        log.error(ex.toString());
        return err;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({OAuth2AuthenticationProcessingException.class})
    public ResponseData<String> handleOAuth2AuthenticationProcessingException(
            OAuth2AuthenticationProcessingException ex) {
        var err = ResponseData.<String>builder()
                .success(false)
                .code(ex.getMessageCode())
                .message(ex.getMessage())
                .error(ex.getCause())
                .build();
        log.error(ex.getMessage());
        log.error(ex.toString());
        return err;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseData<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        var err = ResponseData.<String>builder()
                .success(false)
                .message(ex.getMessage())
                .code(ex.getMessageCode())
                .error(ex.getCause())
                .build();
        log.error(ex.getMessage());
        log.error(ex.toString());
        return err;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({EmailVerificationException.class})
    public ResponseData<String> handleEmailVerificationException(EmailVerificationException ex) {
        return handleInternalError(ex, ex.getMessageCode());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({TokenException.class})
    public ResponseData<String> handleTokenException(TokenException ex) {
        var err = ResponseData.<String>builder()
                .success(false)
                .code(ex.getMessageCode())
                .message(ex.getMessage())
                .error(ex.getCause())
                .build();
        log.error(ex.getMessage());
        log.error(ex.toString());
        return err;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseData<String> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        log.error(ex.getMessage());
        log.error(ex.toString());
        return ResponseUtil.error(ResultCode.MSG_USER_NOT_FOUND.code(), ex.getMessage(), ex.getCause());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseData<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String message = ex.getMessage();
        if (ex.getCause() != null) {
            message = ex.getCause().getMessage();
        }
        log.error(ex.getMessage());
        log.error(ex.toString());
        return ResponseUtil.error(ResultCode.DATA_INTEGRITY_VIOLATION.code(), message, ex.getCause());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({AccessDeniedException.class})
    public ResponseData<String> handleAccessDeniedException(AccessDeniedException ex) {
        var err = ResponseData.<String>builder()
                .success(false)
                .message(ex.getMessage())
                .code(ex.getMessageCode())
                .error(ex.getCause())
                .build();
        log.error(ex.getMessage());
        log.error(ex.toString());
        return err;
    }

    private ResponseData<String> handleInternalError(RuntimeException ex, int messageCode) {
        log.error(ex.getMessage());
        log.error(ex.toString());
        return ResponseUtil.error(messageCode, ex.getMessage(), ex.getCause());
    }
}
