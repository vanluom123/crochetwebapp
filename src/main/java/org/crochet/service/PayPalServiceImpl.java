package org.crochet.service;

import org.crochet.payload.dto.CapturePaymentResponseDTO;
import org.crochet.payload.dto.OrderDTO;
import org.crochet.payload.dto.OrderResponseDTO;
import org.crochet.properties.PayPalProperties;
import org.crochet.service.contact.PayPalService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PayPalServiceImpl implements PayPalService {
    private final WebClient webClient;

    public PayPalServiceImpl(WebClient.Builder builder,
                             PayPalProperties paypalProps) {
        this.webClient = builder.defaultHeaders(header -> {
            header.setBasicAuth(
                    paypalProps.getUsername(),
                    paypalProps.getPassword()
            );
            header.add("Content-Type", "application/json");
        }).build();
    }

    @Override
    public Mono<OrderResponseDTO> createOrder(OrderDTO orderDTO) {
        String uri = "https://api-m.sandbox.paypal.com/v2/checkout/orders";
        return webClient.post()
                .uri(uri)
                .body(Mono.just(orderDTO), OrderDTO.class)
                .retrieve()
                .bodyToMono(OrderResponseDTO.class);
    }

    @Override
    public Mono<CapturePaymentResponseDTO> capturePayment(String orderId) {
        String uri = "https://api-m.sandbox.paypal.com/v2/checkout/orders/" + orderId + "/capture";
        return webClient.post()
                .uri(uri)
                .retrieve()
                .bodyToMono(CapturePaymentResponseDTO.class);
    }
}
