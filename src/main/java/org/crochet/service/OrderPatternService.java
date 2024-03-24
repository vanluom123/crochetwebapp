package org.crochet.service;

import org.crochet.client.paypal.PaymentOrder;
import org.crochet.security.UserPrincipal;

public interface OrderPatternService {
    PaymentOrder createPayment(UserPrincipal principal, String patternId);

    String completePayment(String transactionId);
}
