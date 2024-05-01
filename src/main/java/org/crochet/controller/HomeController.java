package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.crochet.payload.response.HomeResponse;
import org.crochet.service.BannerService;
import org.crochet.service.FreePatternService;
import org.crochet.service.PatternService;
import org.crochet.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {
    private final ProductService productService;
    private final PatternService patternService;
    private final FreePatternService freePatternService;
    private final BannerService bannerService;

    @Operation(summary = "Get home page data")
    @ApiResponse(responseCode = "200", description = "Get home page data successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = HomeResponse.class)))
    @GetMapping
    public ResponseEntity<HomeResponse> getHomes() {
        var products = productService.getLimitedProducts();
        var patterns = patternService.getLimitedPatterns();
        var freePatterns = freePatternService.getLimitedFreePatterns();
        var banners = bannerService.getAll();
        var response = HomeResponse.builder()
                .products(products)
                .patterns(patterns)
                .freePatterns(freePatterns)
                .banners(banners)
                .build();
        return ResponseEntity.ok(response);
    }
}
