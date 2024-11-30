package org.crochet.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CollectionResponse {
    private String id;
    private String name;
    private List<FreePatternOnHome> freePatterns;

    public CollectionResponse(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
