package org.crochet.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductCategoryResponse {
    private String id;
    private String categoryName;
}