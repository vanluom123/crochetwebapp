package org.crochet.mapper;

import lombok.experimental.UtilityClass;
import org.crochet.payload.response.PaginationResponse;
import org.springframework.data.domain.Page;

import java.util.List;

@UtilityClass
public class PaginationMapper {

    public <T> PaginationResponse<T> toPagination(Page<T> page) {
        PaginationResponse<T> response = new PaginationResponse<>();
        response.setContents(page.getContent());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setPageNo(page.getNumber());
        response.setPageSize(page.getSize());
        response.setLast(page.isLast());
        return response;
    }

    public <T> PaginationResponse<T> toPagination(Page<?> page, List<T> contents) {
        PaginationResponse<T> response = new PaginationResponse<>();
        response.setContents(contents);
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setPageNo(page.getNumber());
        response.setPageSize(page.getSize());
        response.setLast(page.isLast());
        return response;
    }
}
