package org.crochet.service.contact;

import org.crochet.payload.dto.OrderDTO;

public interface PayPalService {
    String createOrder(OrderDTO orderDTO);

    String capturePayment(String orderId);
}
