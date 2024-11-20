package org.crochet.payload.response;

import lombok.Data;
import org.crochet.enumerator.ChartStatus;

@Data
public class FreePatternOnHome {
    private String id;
    private String name;
    private String description;
    private String author;
    private ChartStatus status;
    private String fileContent;

    public FreePatternOnHome(String id,
                             String name,
                             String description,
                             String author,
                             ChartStatus status,
                             String fileContent) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.author = author;
        this.status = status;
        this.fileContent = fileContent;
    }
}
