package org.crochet.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CategoryResponse {
    private UUID id;
    private String name;
    private List<CategoryResponse> children;
}