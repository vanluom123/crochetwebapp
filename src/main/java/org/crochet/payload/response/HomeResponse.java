package org.crochet.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeResponse {
    private List<ProductOnHome> products;
    private List<PatternOnHome> patterns;
    private List<FreePatternResponse> freePatterns;
    private List<BannerResponse> banners;
    private List<BlogOnHome> blogs;
}
