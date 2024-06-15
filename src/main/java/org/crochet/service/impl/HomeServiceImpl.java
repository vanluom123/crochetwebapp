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
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {
    private final ProductService productService;
    private final PatternService patternService;
    private final FreePatternService freePatternService;
    private final BannerService bannerService;
    private final BlogPostService blogService;

    @Override
    public HomeResponse getHomes() {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        var prodFuture = CompletableFuture.supplyAsync(productService::getLimitedProducts, executor);
        var patternFuture = CompletableFuture.supplyAsync(patternService::getLimitedPatterns, executor);
        var freePatternFuture = CompletableFuture.supplyAsync(freePatternService::getLimitedFreePatterns, executor);
        var bannerFuture = CompletableFuture.supplyAsync(bannerService::getAll, executor);
        var blogFuture = CompletableFuture.supplyAsync(blogService::getLimitedBlogPosts, executor);

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(prodFuture, patternFuture, freePatternFuture, bannerFuture, blogFuture);

        return allFutures.thenApply(v -> HomeResponse.builder()
                        .products(prodFuture.join())
                        .patterns(patternFuture.join())
                        .freePatterns(freePatternFuture.join())
                        .banners(bannerFuture.join())
                        .blogs(blogFuture.join())
                        .build())
                .exceptionally(this::handleException)
                .join();
    }

    private HomeResponse handleException(Throwable ex) {
        // Log the exception and return a default value
        log.error("Error: ", ex);
        return null; // replace with a default value if needed
    }
}
