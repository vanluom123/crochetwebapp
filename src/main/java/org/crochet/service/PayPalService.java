package org.crochet.service;

import com.paypal.payments.Refund;
import org.crochet.client.paypal.CompleteOrder;
import org.crochet.client.paypal.PaymentOrder;

public interface PayPalService {
    PaymentOrder createPayment(double fee);
    CompleteOrder completePayment(String token);

    Refund refundPayment(String captureId, double refundAmount);
}
