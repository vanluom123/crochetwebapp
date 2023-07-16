package org.crochet.exception;

public class EmailVerificationException extends RuntimeException {
  public EmailVerificationException(String message) {
    super(message);
  }
}
