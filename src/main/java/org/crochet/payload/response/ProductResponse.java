package org.crochet.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private double price;
    private List<String> files;
}
