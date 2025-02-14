package org.crochet.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaginationRequest {
    private int offset;
    private int limit;
    private String orderBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Sort.Direction direction;
    private Filter[] filters;

    public Pageable getPageable() {
        Sort sort = Sort.by(direction, orderBy);
        return PageRequest.of(offset, limit, sort);
    }
}
