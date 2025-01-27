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
    private List<ProductResponse> products;
    private List<PatternResponse> patterns;
    private List<FreePatternResponse> freePatterns;
    private List<BannerResponse> banners;
    private List<BlogPostResponse> blogs;
}
