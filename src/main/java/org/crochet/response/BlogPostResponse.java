package org.crochet.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlogPostResponse {
    private String id;
    private String title;
    private String content;
    private List<String> bytes;
    private LocalDateTime creationDate;
}
