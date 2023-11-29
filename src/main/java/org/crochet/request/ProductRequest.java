package org.crochet.request;

import lombok.Data;

@Data
public class ProductRequest {
    private String id;
    private String name;
    private String description;
    private double price;
}
