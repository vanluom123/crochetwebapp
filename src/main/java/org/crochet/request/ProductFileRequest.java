package org.crochet.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductFileRequest {
    @NotNull
    @NotBlank
    private String productId;
    private String fileUrl;
}
