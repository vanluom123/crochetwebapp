package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.crochet.constant.AppConstant;
import org.crochet.payload.request.Filter;
import org.crochet.payload.request.FreePatternRequest;
import org.crochet.payload.response.FreePatternOnHome;
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.payload.response.PaginatedFreePatternResponse;
import org.crochet.payload.response.ResponseData;
import org.crochet.service.FreePatternService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/free-pattern")
public class FreePatternController {
    private final FreePatternService freePatternService;

    public FreePatternController(FreePatternService freePatternService) {
        this.freePatternService = freePatternService;
    }

    @Operation(summary = "Create a free pattern")
    @ApiResponse(responseCode = "201", description = "Free Pattern created successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = FreePatternResponse.class)))
    @PostMapping(value = "/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<FreePatternResponse> createPattern(
            @RequestBody FreePatternRequest request) {
        var result = freePatternService.createOrUpdate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Get pattern details by ID")
    @ApiResponse(responseCode = "200", description = "Free Pattern details retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = FreePatternResponse.class)))
    @GetMapping("/detail")
    public ResponseEntity<FreePatternResponse> getDetail(
            @Parameter(description = "ID of the pattern to retrieve")
            @RequestParam("id") String id) {
        return ResponseEntity.ok(freePatternService.getDetail(id));
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete a free pattern")
    @ApiResponse(responseCode = "200", description = "Free pattern deleted successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "404", description = "Pattern not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseData<Void> delete(
            @Parameter(description = "ID of the pattern to delete")
            @RequestParam("id") String id) {
        freePatternService.delete(id);
        return new ResponseData<>("Free Pattern deleted successfully", HttpStatus.OK.value(), null);
    }

    @Operation(summary = "Get paginated list of patterns")
    @ApiResponse(responseCode = "200", description = "List of free patterns",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PaginatedFreePatternResponse.class)))
    @PostMapping("/pagination")
    public ResponseEntity<PaginatedFreePatternResponse> getAllFreePatterns(
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
            @Parameter(description = "List filters")
            @RequestBody(required = false) Filter[] filters) {
        var response = freePatternService.getAllFreePatterns(pageNo, pageSize, sortBy, sortDir, filters);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get paginated list of patterns for admin page")
    @ApiResponse(responseCode = "200", description = "List of free patterns",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PaginatedFreePatternResponse.class)))
    @PostMapping("/admin/pagination")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<PaginatedFreePatternResponse> getAllFreePatternsOnAdminPage(
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
            @Parameter(description = "List filters")
            @RequestBody(required = false) Filter[] filters) {
        var response = freePatternService.getAllFreePatternsOnAdminPage(pageNo, pageSize, sortBy, sortDir, filters);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get free pattern ids")
    @ApiResponse(responseCode = "200", description = "List of free pattern ids",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = List.class)))
    @GetMapping("/ids")
    public ResponseEntity<List<String>> getFreePatternIds(@RequestParam("pageNo") int pageNo,
                                                          @RequestParam("limit") int limit) {
        return ResponseEntity.ok(freePatternService.getFreePatternIds(pageNo, limit));
    }

    @Operation(summary = "Get free pattern by create by")
    @ApiResponse(responseCode = "200", description = "List of free pattern",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = List.class)))
    @GetMapping("/create-by")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<List<FreePatternOnHome>> getFrepsByCreateBy() {
        return ResponseEntity.ok(freePatternService.getFrepsByCreateBy());
    }
}
