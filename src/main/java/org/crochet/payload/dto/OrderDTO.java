package org.crochet.payload.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;
import org.crochet.enumerator.OrderIntent;

import java.util.List;

@Data
@Builder
public class OrderDTO {
    private OrderIntent intent;
    @SerializedName("purchase_units")
    private List<PurchaseUnit> purchaseUnits;
    @SerializedName("application_context")
    private PayPalAppContextDTO applicationContext;
}
