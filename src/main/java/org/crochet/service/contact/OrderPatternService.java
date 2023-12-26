package org.crochet.service.contact;

import org.crochet.payload.dto.OrderResponseDTO;

public interface OrderPatternService {
    OrderResponseDTO createPayment(String patternId);

    String capturePayment(String transactionId);
}
