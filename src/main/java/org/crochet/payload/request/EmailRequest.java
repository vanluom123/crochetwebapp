package org.crochet.payload.request;

import lombok.Data;

@Data
public class EmailRequest {
    private String from;
    private String subject;
    private String content;
}
