package org.crochet.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.crochet.enumerator.OrderStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private String id;
    private OrderStatus status;
    private List<LinkDTO> links;

    public Map<String, String> getLinkAsMap() {
        return links.stream()
                .collect(Collectors.toMap(LinkDTO::getRel, LinkDTO::getHref));
    }
}
