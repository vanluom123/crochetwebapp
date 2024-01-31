package org.crochet.service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.crochet.client.paypal.PaymentOrder;
import org.crochet.client.paypal.CompleteOrder;
import org.crochet.service.contact.PayPalService;
import org.springframework.stereotype.Service;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.ApplicationContext;
import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCaptureRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.PurchaseUnitRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@Service
public class PayPalServiceImpl implements PayPalService {
    private final PayPalHttpClient payPalHttpClient;

    public PayPalServiceImpl(PayPalHttpClient payPalHttpClient) {
        this.payPalHttpClient = payPalHttpClient;
    }

    @Override
    public PaymentOrder createPayment(double fee) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");
        AmountWithBreakdown amountBreakdown = new AmountWithBreakdown().currencyCode("USD").value(String.valueOf(fee));
        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest().amountWithBreakdown(amountBreakdown);
        orderRequest.purchaseUnits(List.of(purchaseUnitRequest));
        String baseUri = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        ApplicationContext applicationContext = new ApplicationContext()
                .returnUrl(baseUri + "/api/checkout/order-pattern/success")
                .cancelUrl("https://localhost:3000/cancel");
        orderRequest.applicationContext(applicationContext);
        OrdersCreateRequest ordersCreateRequest = new OrdersCreateRequest().requestBody(orderRequest);
        try {
            HttpResponse<Order> orderHttpResponse = payPalHttpClient.execute(ordersCreateRequest);
            Order order = orderHttpResponse.result();

            String redirectUrl = order.links().stream()
                    .filter(link -> "approve".equals(link.rel()))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new)
                    .href();

            return new PaymentOrder(order.status(), order.id(), redirectUrl);
        } catch (IOException e) {
            log.error(e.getMessage());
            return new PaymentOrder("VOIDED");
        }
    }

    @Override
    public CompleteOrder completePayment(String token) {
        OrdersCaptureRequest ordersCaptureRequest = new OrdersCaptureRequest(token);
        try {
            HttpResponse<Order> httpResponse = payPalHttpClient.execute(ordersCaptureRequest);
            String status = httpResponse.result().status();
            if (status != null) {
                return new CompleteOrder(status, token);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return new CompleteOrder("VOIDED");
    }
}
