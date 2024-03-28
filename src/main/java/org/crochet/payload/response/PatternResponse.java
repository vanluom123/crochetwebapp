package org.crochet.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class PatternResponse {
    private String id;
    private String name;
    private String description;
    private double price;
    @JsonProperty("currency_code")
    private String currencyCode;
    private Set<FileResponse> images;
    private Set<FileResponse> files;
    private CategoryResponse category;
}
