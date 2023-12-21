package org.crochet.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductRequest {
    private String id;
    @NotNull(message = "Product category id cannot be null")
    @NotBlank(message = "Product category id cannot be blank")
    private String productCategoryId;
    private String name;
    private String description;
    private double price;
}
