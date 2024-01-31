package org.crochet.service;

import org.crochet.client.paypal.CompleteOrder;
import org.crochet.client.paypal.PaymentOrder;

public interface PayPalService {
    PaymentOrder createPayment(double fee);
    CompleteOrder completePayment(String token);
}
