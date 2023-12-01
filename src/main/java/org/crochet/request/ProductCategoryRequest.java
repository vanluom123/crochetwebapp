package org.crochet.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductCategoryRequest {
    private String id;
    @NotBlank(message = "category name is not blank")
    @NotNull(message = "category name is not null")
    private String categoryName;
}
