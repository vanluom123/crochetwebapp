package org.crochet.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseException extends RuntimeException {
    private int messageCode;

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, int messageCode) {
        super(message);
        this.messageCode = messageCode;
    }
}
