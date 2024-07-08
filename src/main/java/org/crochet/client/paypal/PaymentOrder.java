package org.crochet.client.paypal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentOrder {
    private String status;
    private String payId;
    private String redirectUrl;

    public PaymentOrder(String status) {
        this.status = status;
    }
}
