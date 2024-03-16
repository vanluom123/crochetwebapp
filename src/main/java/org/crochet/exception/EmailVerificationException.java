package org.crochet.exception;

public class EmailVerificationException extends BaseException {
    public EmailVerificationException(String message) {
        super(message);
    }

    public EmailVerificationException(String message, int messageCode) {
        super(message, messageCode);
    }
}
