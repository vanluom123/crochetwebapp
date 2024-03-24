package org.crochet.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DecoratedRuntimeException extends RuntimeException {
    private int messageCode;

    public DecoratedRuntimeException(String message) {
        super(message);
    }

    public DecoratedRuntimeException(String message, int messageCode) {
        super(message);
        this.messageCode = messageCode;
    }
}
