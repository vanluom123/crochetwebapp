package org.crochet.payload.dto;

import lombok.Data;
import org.crochet.enumerator.OrderStatus;

@Data
public class CapturePaymentResponseDTO {
    private OrderStatus status;
}
