package org.crochet.enumerator;

import lombok.Getter;

@Getter
public enum RoleType {
    USER("USER"),
    ADMIN("ADMIN");

    private final String value;

    RoleType(String value) {
        this.value = value;
    }
}
