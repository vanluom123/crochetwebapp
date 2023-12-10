package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.crochet.response.ProductFileResponse;
import org.crochet.service.contact.ProductFileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/product-file")
public class ProductFileController {
    private final ProductFileService productFileService;

    public ProductFileController(ProductFileService productFileService) {
        this.productFileService = productFileService;
    }

    @Operation(summary = "Create product files")
    @ApiResponse(responseCode = "200", description = "Product files created successfully",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<List<ProductFileResponse>> create(
            @RequestPart("files") MultipartFile[] files,
            @Parameter(description = "ID of the product to associate the files with")
            @RequestParam("productId") String productId) {
        var responses = productFileService.create(files, productId);
        return ResponseEntity.ok(responses);
    }
}
