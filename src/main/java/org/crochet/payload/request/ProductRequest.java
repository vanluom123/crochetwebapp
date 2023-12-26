package org.crochet.payload.request;

import lombok.Builder;
import lombok.Data;
import org.crochet.enumerator.CurrencyCode;

@Data
@Builder
public class ProductRequest {
    private String id;
    private String productCategoryId;
    private String name;
    private String description;
    private double price;
    private CurrencyCode currencyCode;
}
