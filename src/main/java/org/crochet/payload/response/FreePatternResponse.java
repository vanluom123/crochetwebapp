package org.crochet.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.crochet.enumerator.ChartStatus;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "name", "description", "author", "is_home", "link", "content", "status", "images", "files"})
public class FreePatternResponse implements Serializable {
    private String id;
    private String name;
    private String description;
    private String author;
    @JsonProperty("is_home")
    private boolean isHome;
    private String link;
    private String content;
    private ChartStatus status;
    private List<FileResponse> images;
    private List<FileResponse> files;
}
