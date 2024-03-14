package org.crochet.payload.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FreePatternRequest {
    private String id;
    private String name;
    private String description;
    private String author;
    private List<String> photos;
    private List<String> files;
}
