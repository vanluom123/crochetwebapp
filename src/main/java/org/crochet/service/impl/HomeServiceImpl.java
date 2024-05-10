package org.crochet.service.impl;

import lombok.RequiredArgsConstructor;
import org.crochet.payload.response.HomeResponse;
import org.crochet.service.BannerService;
import org.crochet.service.FreePatternService;
import org.crochet.service.HomeService;
import org.crochet.service.PatternService;
import org.crochet.service.ProductService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {
    private final ProductService productService;
    private final PatternService patternService;
    private final FreePatternService freePatternService;
    private final BannerService bannerService;

    @Override
    @Cacheable(value = "gethomes")
    public HomeResponse getHomes() {
        var prods = productService.getLimitedProducts();
        var patterns = patternService.getLimitedPatterns();
        var freePatterns = freePatternService.getLimitedFreePatterns();
        var banners = bannerService.getAll();
        return HomeResponse.builder()
                .products(prods)
                .patterns(patterns)
                .freePatterns(freePatterns)
                .banners(banners)
                .build();
    }
}
