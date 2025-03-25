package org.crochet.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CollectionResponse {
    private String id;
    private String name;
    private String avatar;
    private long totalPatterns;
    private String userId;

    public CollectionResponse(String id,
                              String name,
                              String avatar,
                              long totalPatterns,
                              String userId) {
        this.userId = userId;
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.totalPatterns = totalPatterns;
    }
}
