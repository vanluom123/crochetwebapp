package org.crochet.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FreePatternResponse {
    private String id;
    private String name;
    private String description;
    private List<String> files;
}