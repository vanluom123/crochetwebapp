package org.crochet.service;

import com.google.gson.Gson;
import org.crochet.payload.dto.OrderDTO;
import org.crochet.service.contact.PayPalService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.xiaofeng.webclient.service.WebClientService;
import org.xiaofeng.webclient.type.HttpMethod;

@Service
public class PayPalServiceImpl implements PayPalService {
    @Value("${paypal.username:username not found}")
    private String username;

    @Value("${paypal.password:password not found}")
    private String password;

    private final WebClientService webClientService;
    private final Gson gson;

    public PayPalServiceImpl(WebClientService webClientService, Gson gson) {
        this.webClientService = webClientService;
        this.gson = gson;
    }

    @Override
    public String createOrder(OrderDTO orderDTO) {
        String payload = gson.toJson(orderDTO);
        String uri = "https://api-m.sandbox.paypal.com/v2/checkout/orders";
        return webClientService.invokeApi(uri,
                HttpMethod.POST,
                payload,
                header -> {
                    header.setBasicAuth(username, password);
                    header.setContentType(MediaType.APPLICATION_JSON);
                }).block();
    }

    @Override
    public String capturePayment(String orderId) {
        String uri = "https://api-m.sandbox.paypal.com/v2/checkout/orders/" + orderId + "/capture";
        return webClientService.invokeApi(uri,
                HttpMethod.POST,
                header -> {
                    header.setBasicAuth(username, password);
                    header.setContentType(MediaType.APPLICATION_JSON);
                }).block();
    }
}
