package org.crochet.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.crochet.constant.AppConstant;
import org.crochet.payload.response.HomeResponse;
import org.crochet.service.BannerService;
import org.crochet.service.BlogPostService;
import org.crochet.service.FreePatternService;
import org.crochet.service.HomeService;
import org.crochet.service.PatternService;
import org.crochet.service.ProductService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class HomeServiceImpl implements HomeService {
    private final ProductService productService;
    private final PatternService patternService;
    private final FreePatternService freePatternService;
    private final BannerService bannerService;
    private final BlogPostService blogService;
    private final Executor crochetTaskExecutor;

    public HomeServiceImpl(ProductService productService,
                           PatternService patternService,
                           FreePatternService freePatternService,
                           BannerService bannerService,
                           BlogPostService blogService,
                           @Qualifier(AppConstant.CROCHET_TASK_EXECUTOR) Executor crochetTaskExecutor) {
        this.productService = productService;
        this.patternService = patternService;
        this.freePatternService = freePatternService;
        this.bannerService = bannerService;
        this.blogService = blogService;
        this.crochetTaskExecutor = crochetTaskExecutor;
    }

    /**
     * Fetches all the required data for the home page asynchronously.
     *
     * @return a CompletableFuture of HomeResponse
     */
    @Async(AppConstant.CROCHET_TASK_EXECUTOR)
    @Override
    public CompletableFuture<HomeResponse> getHomesAsync() {
        var prodFuture = CompletableFuture.supplyAsync(productService::getLimitedProducts, crochetTaskExecutor)
                .exceptionally(ex -> {
                    log.error("Failed to fetch products", ex);
                    return Collections.emptyList();
                });
        var patternFuture = CompletableFuture.supplyAsync(patternService::getLimitedPatterns, crochetTaskExecutor)
                .exceptionally(ex -> {
                    log.error("Failed to fetch patterns", ex);
                    return Collections.emptyList();
                });
        var freePatternFuture = CompletableFuture.supplyAsync(freePatternService::getLimitedFreePatterns, crochetTaskExecutor)
                .exceptionally(ex -> {
                    log.error("Failed to fetch free pattern", ex);
                    return Collections.emptyList();
                });
        var bannerFuture = CompletableFuture.supplyAsync(bannerService::getAll, crochetTaskExecutor)
                .exceptionally(ex -> {
                    log.error("Failed to fetch banners", ex);
                    return Collections.emptyList();
                });
        var blogFuture = CompletableFuture.supplyAsync(blogService::getLimitedBlogPosts, crochetTaskExecutor)
                .exceptionally(ex -> {
                    log.error("Failed to fetch blogs", ex);
                    return Collections.emptyList();
                });

        return CompletableFuture.allOf(prodFuture, patternFuture, freePatternFuture, bannerFuture, blogFuture)
                .thenCompose(v -> CompletableFuture.supplyAsync(() -> HomeResponse.builder()
                        .products(prodFuture.join())
                        .patterns(patternFuture.join())
                        .freePatterns(freePatternFuture.join())
                        .banners(bannerFuture.join())
                        .blogs(blogFuture.join())
                        .build(), crochetTaskExecutor));
    }
}
