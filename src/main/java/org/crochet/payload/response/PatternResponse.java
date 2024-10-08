package org.crochet.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "name", "description", "price", "currency_code", "is_home", "link", "content", "images", "files", "category"})
public class PatternResponse implements Serializable {
    private String id;
    private String name;
    private String description;
    private double price;
    @JsonProperty("currency_code")
    private String currencyCode;
    @JsonProperty("is_home")
    private boolean isHome;
    private String link;
    private String content;
    private List<FileResponse> images;
    private List<FileResponse> files;
    private CategoryResponse category;
}
