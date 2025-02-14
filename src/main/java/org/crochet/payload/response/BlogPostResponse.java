package org.crochet.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.crochet.constant.AppConstant;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlogPostResponse {
    private String id;
    private String title;
    private String content;
    @JsonProperty("is_home")
    private Boolean isHome;
    private List<FileResponse> files;
    @JsonFormat(pattern = AppConstant.DATE_PATTERN)
    private LocalDateTime createdDate;
    private String fileContent;
    private String userId;
    private String username;
    private String userAvatar;

    public BlogPostResponse(String id,
                            String title,
                            String content,
                            String fileContent,
                            LocalDateTime createdDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.fileContent = fileContent;
        this.createdDate = createdDate;
    }

    public BlogPostResponse(String id,
                            String title,
                            String content,
                            String fileContent,
                            LocalDateTime createdDate,
                            String userId,
                            String username,
                            String userAvatar) {
        this(id, title, content, fileContent, createdDate);
        this.userId = userId;
        this.username = username;
        this.userAvatar = userAvatar;
    }
}
