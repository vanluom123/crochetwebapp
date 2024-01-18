package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.crochet.constant.AppConstant;
import org.crochet.payload.request.FreePatternRequest;
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.payload.response.PaginatedFreePatternResponse;
import org.crochet.service.contact.FreePatternService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/free-pattern")
public class FreePatternController {
    private final FreePatternService freePatternService;

    public FreePatternController(FreePatternService freePatternService) {
        this.freePatternService = freePatternService;
    }

    @Operation(summary = "Create a pattern")
    @ApiResponse(responseCode = "201", description = "Pattern created successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<String> createPattern(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestPart(required = false) List<MultipartFile> files) {
        var request = FreePatternRequest.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();
        var result = freePatternService.createOrUpdate(request, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Get paginated list of patterns")
    @ApiResponse(responseCode = "200", description = "List of patterns",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PaginatedFreePatternResponse.class)))
    @GetMapping("/pagination")
    public ResponseEntity<PaginatedFreePatternResponse> getPatterns(
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
            @RequestParam(value = "text", required = false) String text) {
        var response = freePatternService.getFreePatterns(pageNo, pageSize, sortBy, sortDir, text);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get pattern details by ID")
    @ApiResponse(responseCode = "200", description = "Pattern details retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = FreePatternResponse.class)))
    @GetMapping("/detail")
    public ResponseEntity<FreePatternResponse> getDetail(
            @Parameter(description = "ID of the pattern to retrieve")
            @RequestParam("id") String id) {
        return ResponseEntity.ok(freePatternService.getDetail(id));
    }
}
