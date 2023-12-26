package org.crochet.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentRequest {
    private String id;
    @NotNull
    @NotBlank
    private String blogPostId;
    private String content;
}
