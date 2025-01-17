package org.crochet.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.crochet.enumerator.ChartStatus;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FreePatternResponse {
    private String id;
    private String name;
    private String description;
    private String author;
    @JsonProperty("is_home")
    private boolean isHome;
    private String link;
    private String content;
    private ChartStatus status;
    private String userId;
    private String username;
    private String userAvatar;
    private List<FileResponse> images;
    private List<FileResponse> files;
    private CategoryResponse category;
}
