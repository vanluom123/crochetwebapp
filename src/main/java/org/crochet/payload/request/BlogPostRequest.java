package org.crochet.payload.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BlogPostRequest {
    private String id;
    private String title;
    private String content;
    private List<String> files;
}
