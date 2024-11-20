package org.crochet.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Embeddable
public class File {
    private String fileName;
    private String fileContent;
    private Integer order;
    private LocalDateTime lastModified;
}
