package org.crochet.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class File {
    private String fileName;
    private String fileContent;
}
