package org.crochet.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductFileResponse {
    private String fileName;
    private String bytes;
}
