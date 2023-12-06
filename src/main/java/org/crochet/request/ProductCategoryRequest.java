package org.crochet.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductCategoryRequest {
    private String id;
    @NotBlank(message = "category name is not blank")
    private String categoryName;
}
