package org.crochet.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.crochet.enumerator.CurrencyCode;

@Data
public class PatternRequest {
    private String id;
    private String name;
    private String description;
    private double price;
    @JsonProperty("currency_code")
    private CurrencyCode currencyCode;
}
