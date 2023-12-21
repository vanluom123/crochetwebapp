package org.crochet.payload.request;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.crochet.enumerator.CurrencyCode;

@Data
public class PatternRequest {
    private String id;
    private String name;
    private String description;
    private double price;
    @SerializedName("currency_code")
    private CurrencyCode currencyCode;
}
