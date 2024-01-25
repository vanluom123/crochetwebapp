package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.crochet.payload.response.HomeResponse;
import org.crochet.service.contact.FreePatternService;
import org.crochet.service.contact.PatternService;
import org.crochet.service.contact.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private ProductService productService;
    @Autowired
    private PatternService patternService;
    @Autowired
    private FreePatternService freePatternService;

    @Operation(summary = "Get home page data")
    @ApiResponse(responseCode = "200", description = "Get home page data successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = HomeResponse.class)))
    @GetMapping
    public ResponseEntity<HomeResponse> getHomes() {
        var products = productService.getLimitedProducts();
        var patterns = patternService.getLimitedPatterns();
        var freePatterns = freePatternService.getLimitedFreePatterns();
        var response = HomeResponse.builder()
                .products(products)
                .patterns(patterns)
                .freePatterns(freePatterns)
                .build();
        return ResponseEntity.ok(response);
    }
}
