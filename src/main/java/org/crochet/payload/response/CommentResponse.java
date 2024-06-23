package org.crochet.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.crochet.constant.AppConstant;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class CommentResponse {
    private String id;
    private String content;
    @JsonFormat(pattern = AppConstant.DATE_PATTERN)
    private LocalDateTime createdDate;
}