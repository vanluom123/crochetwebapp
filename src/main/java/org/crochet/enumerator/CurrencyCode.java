package org.crochet.enumerator;

import lombok.Getter;

@Getter
public enum CurrencyCode {
    USD("USD");

    private final String value;

    CurrencyCode(String value) {
        this.value = value;
    }
}
