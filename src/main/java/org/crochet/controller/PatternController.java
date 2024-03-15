package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.crochet.constant.AppConstant;
import org.crochet.payload.request.PatternRequest;
import org.crochet.payload.response.PatternPaginationResponse;
import org.crochet.payload.response.PatternResponse;
import org.crochet.service.PatternService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pattern")
public class PatternController {
    private final PatternService patternService;

    public PatternController(PatternService patternService) {
        this.patternService = patternService;
    }

    @Operation(summary = "Create a pattern")
    @ApiResponse(responseCode = "201", description = "Pattern created successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<PatternResponse> createPattern(
            @RequestBody PatternRequest request) {
        var result = patternService.createOrUpdate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Get paginated list of patterns")
    @ApiResponse(responseCode = "200", description = "List of patterns",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PatternPaginationResponse.class)))
    @GetMapping("/pagination")
    public ResponseEntity<PatternPaginationResponse> getPatterns(
            @Parameter(description = "Page number (default: 0)")
            @RequestParam(value = "pageNo", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER,
                    required = false) int pageNo,
            @Parameter(description = "Page size (default: 10)")
            @RequestParam(value = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE,
                    required = false) int pageSize,
            @Parameter(description = "Sort by field (default: id)")
            @RequestParam(value = "sortBy", defaultValue = AppConstant.DEFAULT_SORT_BY, required = false) String sortBy,
            @Parameter(description = "Sort direction (default: ASC)")
            @RequestParam(value = "sortDir", defaultValue = AppConstant.DEFAULT_SORT_DIRECTION,
                    required = false) String sortDir,
            @Parameter(description = "Search text")
            @RequestParam(value = "text", required = false) String text,
            @Parameter(description = "Category IDs")
            @RequestParam(value = "categoryIds", required = false) List<UUID> categoryIds) {
        var response = patternService.getPatterns(pageNo, pageSize, sortBy, sortDir, text, categoryIds);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get pattern details by ID")
    @ApiResponse(responseCode = "200", description = "Pattern details retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PatternResponse.class)))
    @GetMapping("/detail")
    public ResponseEntity<PatternResponse> getDetail(
            @Parameter(description = "ID of the pattern to retrieve")
            @RequestParam("id") String id) {
        return ResponseEntity.ok(patternService.getDetail(id));
    }

    @Operation(summary = "Delete a pattern")
    @ApiResponse(responseCode = "200", description = "Pattern deleted successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "Pattern not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<String> deletePattern(
            @Parameter(description = "ID of the pattern to delete")
            @RequestParam("id") UUID id) {
        patternService.deletePattern(id);
        return ResponseEntity.ok("Pattern deleted successfully");
    }
}
