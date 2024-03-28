package org.crochet.exception;

public class IllegalArgumentException extends DecoratedRuntimeException {
    public IllegalArgumentException(String message) {
        super(message);
    }

    public IllegalArgumentException(String message, int messageCode) {
        super(message, messageCode);
    }
}
