package org.crochet.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;
import org.crochet.payload.response.FileResponse;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@JsonPropertyOrder({"id", "category_id", "name", "description", "author", "is_home", "link", "isBanner", "images", "files"})
public class FreePatternRequest {
    private UUID id;
    @JsonProperty("category_id")
    private UUID categoryId;
    private String name;
    private String description;
    private String author;
    @JsonProperty("is_home")
    private boolean isHome;
    private String link;
    private boolean isBanner;
    private List<FileResponse> images;
    private List<FileResponse> files;
}
