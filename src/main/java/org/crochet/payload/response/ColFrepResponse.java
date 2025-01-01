package org.crochet.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ColFrepResponse {
    @JsonProperty("collection_id")
    private String colId;
    @JsonProperty("free_pattern_id")
    private String frepId;
}
