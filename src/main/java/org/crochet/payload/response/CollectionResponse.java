package org.crochet.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import java.util.ArrayList;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionResponse {
    private String id;
    private String name;
    private int totalPatterns;
    private List<FreePatternOnHome> freePatterns;

    public CollectionResponse(String id, String name) {
        this.id = id;
        this.name = name;
        this.totalPatterns = 0;
        this.freePatterns = new ArrayList<>();
    }
}
