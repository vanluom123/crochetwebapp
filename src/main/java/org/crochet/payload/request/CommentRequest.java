package org.crochet.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CommentRequest {
    private UUID id;
    @NotNull
    @NotBlank
    private UUID blogPostId;
    private String content;
}
