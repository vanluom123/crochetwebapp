package org.crochet.client.paypal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompleteOrder {
    private String status;
    private String payId;

    public CompleteOrder(String status) {
        this.status = status;
    }
}
