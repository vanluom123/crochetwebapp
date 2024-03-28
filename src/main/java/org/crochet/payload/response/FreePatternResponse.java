package org.crochet.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class FreePatternResponse {
    private String id;
    private String name;
    private String description;
    private String author;
    private Set<FileResponse> images;
    private Set<FileResponse> files;
    private CategoryResponse category;
}
