package org.crochet.payload.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BlogPostResponse {
    private String id;
    private String title;
    private String content;
    private List<String> bytes;
    private LocalDateTime creationDate;
}
