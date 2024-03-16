package org.crochet.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.crochet.model.File;
import org.crochet.model.Image;
import org.crochet.payload.response.FileResponse;

import java.util.List;
import java.util.Set;
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
    private Set<FileResponse> images;
    private Set<FileResponse> files;
}
