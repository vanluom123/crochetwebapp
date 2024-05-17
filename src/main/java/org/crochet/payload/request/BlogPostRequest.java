package org.crochet.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.crochet.payload.response.FileResponse;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostRequest {
    private String id;
    private String title;
    private String content;
    @JsonProperty("is_home")
    private boolean isHome;
    private List<FileResponse> files;
}
