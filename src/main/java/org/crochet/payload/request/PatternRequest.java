package org.crochet.payload.request;

import lombok.Builder;
import lombok.Data;
import org.crochet.enumerator.CurrencyCode;

import java.util.List;

@Data
@Builder
public class PatternRequest {
    private String id;
    private String name;
    private String description;
    private double price;
    private CurrencyCode currencyCode;
    private List<String> files;
}
