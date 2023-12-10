package org.crochet.request;

import lombok.Data;

@Data
public class BlogPostRequest {
    private String id;
    private String title;
    private String content;
}
