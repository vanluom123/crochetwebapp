package org.crochet.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.crochet.constant.AppConstant;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BannerResponse {
    private String id;
    private String title;
    private String content;
    private String url;
    private String fileName;
    private String fileContent;
    private boolean active;
    private String textColor;
    private BannerTypeResponse bannerType;
    @JsonFormat(pattern = AppConstant.DATE_PATTERN)
    private LocalDateTime createdDate;
    @JsonFormat(pattern = AppConstant.DATE_PATTERN)
    private LocalDateTime lastModifiedDate;
}