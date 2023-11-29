package org.crochet.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ProductFileResponse {
    private String fileUrl;
    private ProductResponse product;
}
