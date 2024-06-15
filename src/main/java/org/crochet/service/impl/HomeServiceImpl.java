package org.crochet.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.payload.response.HomeResponse;
import org.crochet.service.BannerService;
import org.crochet.service.BlogPostService;
import org.crochet.service.FreePatternService;
import org.crochet.service.HomeService;
import org.crochet.service.PatternService;
import org.crochet.service.ProductService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {
    private final ProductService productService;
    private final PatternService patternService;
    private final FreePatternService freePatternService;
    private final BannerService bannerService;
    private final BlogPostService blogService;

    @Async("taskExecutor")
    @Override
    public CompletableFuture<HomeResponse> getHomesAsync() {
        var prodFuture = CompletableFuture.supplyAsync(productService::getLimitedProducts);
        var patternFuture = CompletableFuture.supplyAsync(patternService::getLimitedPatterns);
        var freePatternFuture = CompletableFuture.supplyAsync(freePatternService::getLimitedFreePatterns);
        var bannerFuture = CompletableFuture.supplyAsync(bannerService::getAll);
        var blogFuture = CompletableFuture.supplyAsync(blogService::getLimitedBlogPosts);

        return CompletableFuture.allOf(prodFuture, patternFuture, freePatternFuture, bannerFuture, blogFuture)
                .thenApply(v -> HomeResponse.builder()
                        .products(prodFuture.join())
                        .patterns(patternFuture.join())
                        .freePatterns(freePatternFuture.join())
                        .banners(bannerFuture.join())
                        .blogs(blogFuture.join())
                        .build())
                .exceptionally(this::handleException);
    }

    private HomeResponse handleException(Throwable ex) {
        // Log the exception and return a default value
        log.error("Error occurred while fetching home data: ", ex);
        return HomeResponse.builder()
                .products(Collections.emptyList())
                .patterns(Collections.emptyList())
                .freePatterns(Collections.emptyList())
                .banners(Collections.emptyList())
                .blogs(Collections.emptyList())
                .build();
    }
}
