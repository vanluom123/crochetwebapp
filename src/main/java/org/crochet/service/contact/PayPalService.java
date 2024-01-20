package org.crochet.service.contact;

import org.crochet.payload.dto.CapturePaymentResponseDTO;
import org.crochet.payload.dto.OrderDTO;
import org.crochet.payload.dto.OrderResponseDTO;
import reactor.core.publisher.Mono;

public interface PayPalService {
    Mono<OrderResponseDTO> createOrder(OrderDTO orderDTO);

    Mono<CapturePaymentResponseDTO> capturePayment(String orderId);
}
