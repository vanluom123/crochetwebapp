package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.crochet.payload.response.HomeResponse;
import org.crochet.service.FreePatternService;
import org.crochet.service.PatternService;
import org.crochet.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

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

    @Operation(summary = "Setting home page")
    @ApiResponse(responseCode = "200", description = "Setting home page successfully")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/setting")
    public ResponseEntity<String> setting(
            @RequestParam(required = false) UUID productId,
            @RequestParam(required = false) boolean isHomeProduct,
            @RequestParam(required = false) UUID patternId,
            @RequestParam(required = false) boolean isHomePattern,
            @RequestParam(required = false) UUID freePatternId,
            @RequestParam(required = false) boolean isHomeFreePattern) {
        productService.updateHomeStatus(productId, isHomeProduct);
        patternService.updateHomeStatus(patternId, isHomePattern);
        freePatternService.updateHomeStatus(freePatternId, isHomeFreePattern);
        return ResponseEntity.ok("Setting home page successfully");
    }
}
