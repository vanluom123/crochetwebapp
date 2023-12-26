package org.crochet.payload.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;
import org.crochet.enumerator.PaymentLandingPage;

@Data
@Builder
public class PayPalAppContextDTO {
    @SerializedName("brand_name")
    private String brandName;
    @SerializedName("landing_page")
    private PaymentLandingPage landingPage;
    @SerializedName("return_url")
    private String returnUrl;
    @SerializedName("cancel_url")
    private String cancelUrl;
}
