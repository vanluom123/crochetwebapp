package org.crochet.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryCreationRequest {
    private List<String> parentIds;
    @NotBlank(message = "Name is not blank")
    private String name;
}