package org.crochet.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PatternResponse {
    private String id;
    private String name;
    private String description;
    private double price;
    @JsonProperty("currency_code")
    private String currencyCode;
    private List<FileResponse> images;
    private List<FileResponse> files;
    private CategoryResponse category;
}
