package org.crochet.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
    private List<FreePatternResponse> freePatterns;

    public CollectionResponse(String id, String name, String avatar, long totalPatterns) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.totalPatterns = totalPatterns;
    }
}
