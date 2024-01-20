package org.crochet.service;

import com.google.gson.Gson;
import org.crochet.payload.dto.OrderDTO;
import org.crochet.properties.PayPalProperties;
import org.crochet.service.contact.PayPalService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PayPalServiceImpl implements PayPalService {
    private final WebClient webClient;
    private final Gson gson;

    public PayPalServiceImpl(WebClient.Builder builder,
                             Gson gson,
                             PayPalProperties paypalProps) {
        this.webClient = builder.defaultHeaders(header -> {
            header.setBasicAuth(
                    paypalProps.getUsername(),
                    paypalProps.getPassword()
            );
            header.add("Content-Type", "application/json");
        }).build();
        this.gson = gson;
    }

    @Override
    public Mono<String> createOrder(OrderDTO orderDTO) {
        String payload = gson.toJson(orderDTO);
        String uri = "https://api-m.sandbox.paypal.com/v2/checkout/orders";
        return webClient.post()
                .uri(uri)
                .body(Mono.just(payload), String.class)
                .retrieve()
                .bodyToMono(String.class);
    }

    @Override
    public Mono<String> capturePayment(String orderId) {
        String uri = "https://api-m.sandbox.paypal.com/v2/checkout/orders/" + orderId + "/capture";
        return webClient.post()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class);
    }
}
