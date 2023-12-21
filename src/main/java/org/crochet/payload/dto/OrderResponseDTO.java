package org.crochet.payload.dto;

import lombok.Builder;
import lombok.Data;
import org.crochet.enumerator.OrderStatus;

import java.util.List;

@Data
@Builder
public class OrderResponseDTO {
    private String id;
    private OrderStatus status;
    private List<LinkDTO> links;
}
