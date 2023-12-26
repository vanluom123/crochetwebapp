package org.crochet.payload.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FreePatternRequest {
    private String id;
    private String name;
    private String description;
}
