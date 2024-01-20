package org.crochet.payload.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductCategoryRequest {
    private String id;
    private String categoryName;
    private String parentCategoryName;
}
