package org.crochet.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.crochet.enums.CurrencyCode;
import org.crochet.payload.response.FileResponse;

import java.util.List;

@Data
@Builder
public class ProductRequest {
    private String id;
    @JsonProperty("category_id")
    private String categoryId;
    private String name;
    private String description;
    private double price;
    @JsonProperty("currency_code")
    private CurrencyCode currencyCode;
    @JsonProperty("is_home")
    private boolean isHome;
    private String link;
    private String content;
    private List<FileResponse> images;
}
