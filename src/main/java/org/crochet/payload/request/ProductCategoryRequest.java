package org.crochet.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductCategoryRequest {
    private String id;
    @NotBlank
    @NotNull
    private String categoryName;
    private String parentCategoryName;
}
