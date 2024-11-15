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
        var prodFuture = CompletableFuture.supplyAsync(productService::getLimitedProducts, crochetTaskExecutor);
        var patternFuture = CompletableFuture.supplyAsync(patternService::getLimitedPatterns, crochetTaskExecutor);
        var freePatternFuture = CompletableFuture.supplyAsync(freePatternService::getLimitedFreePatterns, crochetTaskExecutor);
        var bannerFuture = CompletableFuture.supplyAsync(bannerService::getAll, crochetTaskExecutor);
        var blogFuture = CompletableFuture.supplyAsync(blogService::getLimitedBlogPosts, crochetTaskExecutor);

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

    /**
     * Handles the exception occurred while fetching home data.
     *
     * @param ex the exception occurred
     * @return a default HomeResponse
     */
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
