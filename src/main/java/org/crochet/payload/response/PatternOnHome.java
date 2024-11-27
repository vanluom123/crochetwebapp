package org.crochet.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.crochet.enumerator.CurrencyCode;

@AllArgsConstructor
@Data
public class PatternOnHome {
    private String id;
    private String name;
    private String description;
    private double price;
    private CurrencyCode currencyCode;
    private String imageUrl;
}
