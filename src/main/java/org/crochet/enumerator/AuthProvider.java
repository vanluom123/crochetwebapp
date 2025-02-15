package org.crochet.enumerator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthProvider {
    LOCAL("LOCAL"),
    FACEBOOK("FACEBOOK"),
    GOOGLE("GOOGLE"),
    GITHUB("GITHUB");

    private final String value;
}
