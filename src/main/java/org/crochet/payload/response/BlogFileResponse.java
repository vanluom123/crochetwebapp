package org.crochet.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlogFileResponse {
    private String id;
    private String fileName;
    private String bytes;
}