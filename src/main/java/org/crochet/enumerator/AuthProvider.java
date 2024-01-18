package org.crochet.enumerator;

import lombok.Getter;

@Getter
public enum AuthProvider {
    LOCAL("LOCAL"),
    FACEBOOK("FACEBOOK"),
    GOOGLE("GOOGLE"),
    GITHUB("GITHUB");

    private final String value;

    AuthProvider(String value) {
        this.value = value;
    }
}
