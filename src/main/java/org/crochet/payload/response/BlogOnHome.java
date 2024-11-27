package org.crochet.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.crochet.constant.AppConstant;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BlogOnHome {
    private String id;
    private String title;
    private String content;
    private String fileContent;
    @JsonFormat(pattern = AppConstant.DATE_PATTERN)
    private LocalDateTime createdDate;
}
