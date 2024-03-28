package org.crochet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends DecoratedRuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, int messageCode) {
        super(message, messageCode);
    }
}
