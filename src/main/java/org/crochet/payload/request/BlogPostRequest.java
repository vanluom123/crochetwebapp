package org.crochet.payload.request;

import lombok.Builder;
import lombok.Data;
import org.crochet.payload.response.FileResponse;

import java.util.List;

@Data
@Builder
public class BlogPostRequest {
    private String id;
    private String title;
    private String content;
    private List<FileResponse> files;
}
