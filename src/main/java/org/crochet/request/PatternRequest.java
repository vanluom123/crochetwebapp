package org.crochet.request;

import lombok.Data;

@Data
public class PatternRequest {
    private String id;
    private String name;
    private String description;
    private double price;
}
