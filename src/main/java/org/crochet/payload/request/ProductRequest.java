package org.crochet.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.crochet.enumerator.CurrencyCode;

@Data
public class ProductRequest {
    private String id;
    @NotNull(message = "Product category id cannot be null")
    @NotBlank(message = "Product category id cannot be blank")
    private String productCategoryId;
    private String name;
    private String description;
    private double price;
    @JsonProperty("currency_code")
    private CurrencyCode currencyCode;
}
