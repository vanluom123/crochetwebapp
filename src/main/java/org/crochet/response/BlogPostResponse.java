package org.crochet.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlogPostResponse {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private LocalDateTime creationDate;
}
