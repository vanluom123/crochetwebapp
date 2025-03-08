package org.crochet.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChartStatus {
    SUCCESS("SUCCESS"),
    PENDING("PENDING"),
    NONE("NONE");

    private final String value;
}
