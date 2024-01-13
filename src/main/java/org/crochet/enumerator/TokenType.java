package org.crochet.enumerator;

import lombok.Getter;

@Getter
public enum TokenType {
  BEARER("Bearer");

  TokenType(String value) {
    this.value = value;
  }

  private final String value;
}
