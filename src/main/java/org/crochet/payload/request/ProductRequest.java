package org.crochet.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.crochet.enumerator.CurrencyCode;

import java.util.List;

@Data
@Builder
public class ProductRequest {
    private String id;
    @NotBlank
    @NotNull
    @JsonProperty("product_category_id")
    private String productCategoryId;
    private String name;
    private String description;
    private double price;
    @JsonProperty("currency_code")
    private CurrencyCode currencyCode;
    private List<String> files;
}
