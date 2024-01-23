package org.crochet.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryCreationWithParentRequest {
    @NotBlank(message = "Name is not blank")
    private String name;
    @NotBlank(message = "Name is not blank")
    private String parentName;
}