package org.crochet.service.contact;

import org.crochet.payload.dto.OrderDTO;
import reactor.core.publisher.Mono;

public interface PayPalService {
    Mono<String> createOrder(OrderDTO orderDTO);

    Mono<String> capturePayment(String orderId);
}
