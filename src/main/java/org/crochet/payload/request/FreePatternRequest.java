package org.crochet.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class FreePatternRequest {
    private String id;
    @JsonProperty("category_id")
    private UUID categoryId;
    private String name;
    private String description;
    private String author;
    private List<String> photos;
    private List<String> files;
}
