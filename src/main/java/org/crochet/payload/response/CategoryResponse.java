package org.crochet.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CategoryResponse implements Serializable {
    private String id;
    private String name;
    private List<CategoryResponse> children;
}