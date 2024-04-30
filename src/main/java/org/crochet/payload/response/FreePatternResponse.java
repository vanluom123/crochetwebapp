package org.crochet.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonPropertyOrder({"id", "name", "description", "author", "is_home", "link", "content", "images", "files", "category"})
public class FreePatternResponse {
    private String id;
    private String name;
    private String description;
    private String author;
    @JsonProperty("is_home")
    private boolean isHome;
    private String link;
    private String content;
    private List<FileResponse> images;
    private List<FileResponse> files;
    private CategoryResponse category;
}
