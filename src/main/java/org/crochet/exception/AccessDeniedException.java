package org.crochet.exception;

public class AccessDeniedException extends DecoratedRuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, int messageCode) {
        super(message, messageCode);
    }
}
