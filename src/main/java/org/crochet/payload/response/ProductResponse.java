package org.crochet.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonPropertyOrder({"id", "name", "description", "price", "currency_code", "is_home", "isBanner", "isCoveredBackground", "link", "images", "category"})
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private double price;
    @JsonProperty("currency_code")
    private String currencyCode;
    @JsonProperty("is_home")
    private boolean isHome;
    private String link;
    private boolean isBanner;
    private boolean isCoveredBackground;
    private List<FileResponse> images;
    private CategoryResponse category;
}
