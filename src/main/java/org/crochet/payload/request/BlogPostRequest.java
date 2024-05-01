package org.crochet.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.crochet.payload.response.FileResponse;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostRequest {
    private UUID id;
    private String title;
    private String content;
    private boolean home;
    private List<FileResponse> files;
    private List<FileResponse> avatars;
}
