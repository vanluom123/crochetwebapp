package org.crochet.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class OAuth2AuthenticationProcessingException extends AuthenticationException {
    private int messageCode;

    public OAuth2AuthenticationProcessingException(String msg) {
        super(msg);
    }

    public OAuth2AuthenticationProcessingException(String msg, int messageCode) {
        super(msg);
        this.messageCode = messageCode;
    }

}
