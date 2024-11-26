package org.crochet.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.crochet.constant.AppConstant;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class FileResponse {
    private String fileName;
    private String fileContent;
    private Integer order;
    @JsonFormat(pattern = AppConstant.DATE_PATTERN)
    private LocalDateTime lastModified;

    public FileResponse(String fileName, String fileContent) {
        this(fileName, fileContent, 0);
    }

    public FileResponse(String fileName, String fileContent, Integer order) {
        this(fileName, fileContent, order, LocalDateTime.now());
    }

    public FileResponse(String fileName, String fileContent, LocalDateTime lastModified) {
        this(fileName, fileContent, 0, lastModified);
    }

    public FileResponse(String fileName, String fileContent, Integer order, LocalDateTime lastModified) {
        this.fileName = fileName;
        this.fileContent = fileContent;
        this.order = order;
        this.lastModified = lastModified;
    }
}
