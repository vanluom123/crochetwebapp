package org.crochet.payload.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CollectionResponse {
    private String id;
    private String name;
    private String avatar;
    private long totalPatterns;
    private List<FreePatternOnHome> freePatterns;

    public CollectionResponse(String id, String name, String avatar, long totalPatterns) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.totalPatterns = totalPatterns;
    }
}
