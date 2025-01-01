package org.crochet.payload.request;

import lombok.Data;

@Data
public class UpdateCollectionRequest {
    private String name;
    private String description;
}
