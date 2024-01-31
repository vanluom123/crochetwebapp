package org.crochet.service.contact;

import org.crochet.client.paypal.PaymentOrder;

public interface OrderPatternService {
    PaymentOrder createPayment(String patternId);

    String completePayment(String transactionId);
}
