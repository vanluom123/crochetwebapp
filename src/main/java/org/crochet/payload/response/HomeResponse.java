package org.crochet.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HomeResponse {
    private List<ProductResponse> products;
    private List<PatternResponse> patterns;
    private List<FreePatternResponse> freePatterns;
    private List<BannerResponse> banners;
}
