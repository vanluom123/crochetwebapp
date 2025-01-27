package org.crochet.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.crochet.enumerator.CurrencyCode;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatternResponse {
    private String id;
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
    private List<FileResponse> files;
    private CategoryResponse category;
    private String fileContent;

    public PatternResponse(String id,
                           String name,
                           String description,
                           double price,
                           CurrencyCode currencyCode,
                           String fileContent) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.currencyCode = currencyCode;
        this.fileContent = fileContent;
    }
}
