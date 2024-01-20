package org.crochet.payload.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MoneyDTO {
    @JsonProperty("currency_code")
    private String currencyCode;
    private String value;
}