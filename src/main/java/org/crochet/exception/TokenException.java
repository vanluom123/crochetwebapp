package org.crochet.exception;

public class TokenException extends DecoratedRuntimeException {
    public TokenException(String tokenExpired) {
        super(tokenExpired);
    }

    public TokenException(String message, int messageCode) {
        super(message, messageCode);
    }
}
