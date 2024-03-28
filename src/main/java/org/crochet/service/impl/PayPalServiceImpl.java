package org.crochet.service.impl;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import com.paypal.payments.CapturesRefundRequest;
import com.paypal.payments.Money;
import com.paypal.payments.Refund;
import com.paypal.payments.RefundRequest;
import lombok.extern.slf4j.Slf4j;
import org.crochet.client.paypal.CompleteOrder;
import org.crochet.client.paypal.PaymentOrder;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.service.PayPalService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class PayPalServiceImpl implements PayPalService {
    public static final String USD = "USD";
    public static final String CAPTURE = "CAPTURE";
    public static final String VOIDED = "VOIDED";
    public static final String APPROVE = "approve";
    public static final String LINK_NOT_FOUND_MESSAGE = "approve link not found";
    private final PayPalHttpClient payPalHttpClient;

    public PayPalServiceImpl(PayPalHttpClient payPalHttpClient) {
        this.payPalHttpClient = payPalHttpClient;
    }

    @Override
    public PaymentOrder createPayment(double fee) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent(CAPTURE);
        AmountWithBreakdown amountBreakdown = new AmountWithBreakdown().currencyCode(USD).value(String.valueOf(fee));
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
                    .filter(link -> APPROVE.equals(link.rel()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException(LINK_NOT_FOUND_MESSAGE))
                    .href();

            return new PaymentOrder(order.status(), order.id(), redirectUrl);
        } catch (IOException e) {
            log.error(e.getMessage());
            return new PaymentOrder(VOIDED);
        }
    }

    @Override
    public CompleteOrder completePayment(String token) {
        OrdersCaptureRequest ordersCaptureRequest = new OrdersCaptureRequest(token);
        try {
            HttpResponse<Order> httpResponse = payPalHttpClient.execute(ordersCaptureRequest);
            Order orderResult = httpResponse.result();
            String status = orderResult.status();
            if (status != null) {
                return new CompleteOrder(status, token);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return new CompleteOrder(VOIDED);
    }

    @Override
    public Refund refundPayment(String captureId, double refundAmount) {
        RefundRequest request = new RefundRequest();
        Money money = new Money();
        money.currencyCode(USD);
        money.value(String.valueOf(refundAmount));
        request.amount(money);

        CapturesRefundRequest capturesRefundRequest = new CapturesRefundRequest(captureId).requestBody(request);
        try {
            HttpResponse<Refund> refundHttpResponse = payPalHttpClient.execute(capturesRefundRequest);
            return refundHttpResponse.result();
        } catch (IOException e) {
            log.error(e.getMessage());
            return new Refund();
        }
    }
}
