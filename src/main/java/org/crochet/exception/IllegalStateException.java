package org.crochet.exception;

public class IllegalStateException extends DecoratedRuntimeException {
    public IllegalStateException(String message) {
        super(message);
    }

    public IllegalStateException(String message, int messageCode) {
        super(message, messageCode);
    }
}
