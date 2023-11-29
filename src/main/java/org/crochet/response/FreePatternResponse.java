package org.crochet.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class FreePatternResponse {
    private long id;
    private String name;
    private String description;
    private List<String> images;
}
