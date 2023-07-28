package org.crochet.exception;

public class TokenException extends RuntimeException {
  public TokenException(String tokenExpired) {
    super(tokenExpired);
  }

}
