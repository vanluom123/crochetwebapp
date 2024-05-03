package org.crochet.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryUpdateRequest {
    private String id;
    @NotBlank(message = "Name is not blank")
    private String name;
}