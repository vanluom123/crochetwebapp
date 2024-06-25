package org.crochet.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.crochet.constant.AppConstant;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostResponse implements Serializable {
    private String id;
    private String title;
    private String content;
    @JsonProperty("is_home")
    private boolean isHome;
    private List<FileResponse> files;
    @JsonFormat(pattern = AppConstant.DATE_PATTERN)
    private LocalDateTime createdDate;
}
