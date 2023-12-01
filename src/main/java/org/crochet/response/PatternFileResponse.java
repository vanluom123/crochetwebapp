package org.crochet.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PatternFileResponse {
    private String id;
    private String fileName;
    private String bytes;
}