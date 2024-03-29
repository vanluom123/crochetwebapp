package org.crochet.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.crochet.enumerator.CurrencyCode;
import org.crochet.payload.response.FileResponse;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ProductRequest {
    private UUID id;
    @JsonProperty("category_id")
    private UUID categoryId;
    private String name;
    private String description;
    private double price;
    @JsonProperty("currency_code")
    private CurrencyCode currencyCode;
    private List<FileResponse> files;
}
