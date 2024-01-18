package org.crochet.service;

import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import org.crochet.payload.dto.OrderDTO;
import org.crochet.properties.PayPalProperties;
import org.crochet.service.contact.PayPalService;
import org.springframework.stereotype.Service;
import org.xiaofeng.webclient.service.WebClientService;
import org.xiaofeng.webclient.type.HttpMethod;

@Service
public class PayPalServiceImpl implements PayPalService {
    private final WebClientService webClientService;
    private final Gson gson;
    private final PayPalProperties paypalProps;

    public PayPalServiceImpl(WebClientService webClientService,
                             Gson gson,
                             PayPalProperties paypalProps) {
        this.webClientService = webClientService;
        this.gson = gson;
        this.paypalProps = paypalProps;
    }

    @PostConstruct
    public void init() {
        webClientService.builder()
                .defaultHeaders(header -> header.setBasicAuth(
                        paypalProps.getUsername(),
                        paypalProps.getPassword()
                ));
    }

    @Override
    public String createOrder(OrderDTO orderDTO) {
        String payload = gson.toJson(orderDTO);
        String uri = "https://api-m.sandbox.paypal.com/v2/checkout/orders";
        return webClientService.invokeApi(uri,
                HttpMethod.POST,
                payload).block();
    }

    @Override
    public String capturePayment(String orderId) {
        String uri = "https://api-m.sandbox.paypal.com/v2/checkout/orders/" + orderId + "/capture";
        return webClientService.invokeApi(uri,
                HttpMethod.POST).block();
    }
}
