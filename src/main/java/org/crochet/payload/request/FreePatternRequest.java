package org.crochet.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.crochet.enumerator.ChartStatus;
import org.crochet.payload.response.FileResponse;

import java.util.List;

@Data
@Builder
public class FreePatternRequest {
    private String id;
    @JsonProperty("category_id")
    private String categoryId;
    private String name;
    private String description;
    private String author;
    @JsonProperty("is_home")
    private boolean isHome;
    private String link;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private ChartStatus status;
    private List<FileResponse> images;
    private List<FileResponse> files;
}
