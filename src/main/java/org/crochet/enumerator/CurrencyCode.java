package org.crochet.enumerator;

import lombok.Getter;

@Getter
public enum CurrencyCode {
    USD("USD");

    CurrencyCode(String value) {
        this.value = value;
    }

    private final String value;
}
