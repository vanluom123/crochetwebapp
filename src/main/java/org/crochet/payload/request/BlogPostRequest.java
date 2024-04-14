package org.crochet.payload.request;

import lombok.Builder;
import lombok.Data;
import org.crochet.payload.response.FileResponse;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class BlogPostRequest {
    private UUID id;
    private String title;
    private String content;
    private List<FileResponse> files;
}
