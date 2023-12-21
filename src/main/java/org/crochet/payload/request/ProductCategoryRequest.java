package org.crochet.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductCategoryRequest {
    private String id;

    @NotNull(message = "category name cannot be null")
    @NotBlank(message = "category name cannot be blank")
    private String categoryName;
}
