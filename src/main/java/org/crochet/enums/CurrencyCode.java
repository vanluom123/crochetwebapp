package org.crochet.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CurrencyCode {
    USD("USD"),
    VND("VND");

    private final String value;
}
