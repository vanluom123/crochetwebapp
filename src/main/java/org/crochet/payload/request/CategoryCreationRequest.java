package org.crochet.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CategoryCreationRequest {
    private List<UUID> parentIds;
    @NotBlank(message = "Name is not blank")
    private String name;
}