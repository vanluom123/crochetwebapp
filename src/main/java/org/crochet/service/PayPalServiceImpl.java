package org.crochet.service;

import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.crochet.payload.dto.OrderDTO;
import org.crochet.service.contact.PayPalService;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PayPalServiceImpl implements PayPalService {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final Gson gson;
    private final OkHttpClient client;

    public PayPalServiceImpl(Gson gson, OkHttpClient client) {
        this.gson = gson;
        this.client = client;
    }

    @Override
    public String createOrder(OrderDTO orderDTO) {
        String payload = gson.toJson(orderDTO);
        String uri = "https://api-m.sandbox.paypal.com/v2/checkout/orders";
        RequestBody requestBody = RequestBody.create(payload, JSON);
        Request request = new Request.Builder()
                .url(uri)
                .post(requestBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                return responseBody.string();
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String capturePayment(String orderId) {
        String uri = "https://api-m.sandbox.paypal.com/v2/checkout/orders/" + orderId + "/capture";
        RequestBody requestBody = RequestBody.create(JSON, new byte[0]);
        Request request = new Request.Builder()
                .url(uri)
                .post(requestBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                return responseBody.string();
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
