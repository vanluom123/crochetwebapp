package org.crochet.enumerator;

import lombok.Getter;

@Getter
public enum ChartStatus {
    SUCCESS("SUCCESS"),
    PENDING("PENDING"),
    NONE("NONE");

    private final String value;

    ChartStatus(String value) {
        this.value = value;
    }
}
