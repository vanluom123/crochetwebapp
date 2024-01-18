package org.crochet.enumerator;

import lombok.Getter;

@Getter
public enum OrderStatus {
    CREATED("CREATED"),
    SAVED("SAVED"),
    APPROVED("APPROVED"),
    VOIDED("VOIDED"),
    COMPLETED("COMPLETED"),
    PAYER_ACTION_REQUIRED("PAYER_ACTION_REQUIRED");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }
}
