package org.crochet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class File {
    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_content", columnDefinition = "TEXT")
    private String fileContent;
}
