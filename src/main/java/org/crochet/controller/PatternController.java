package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.crochet.constant.AppConstant;
import org.crochet.payload.request.Filter;
import org.crochet.payload.request.PatternRequest;
import org.crochet.payload.response.PaginationResponse;
import org.crochet.payload.response.PatternResponse;
import org.crochet.payload.response.ResponseData;
import org.crochet.service.PatternService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.crochet.constant.AppConstant.SUCCESS;

@RestController
@RequestMapping("/api/v1/patterns")
public class PatternController {
    private final PatternService patternService;

    public PatternController(PatternService patternService) {
        this.patternService = patternService;
    }

    @Operation(summary = "Create a pattern")
    @ApiResponse(responseCode = "201", description = "Pattern created successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PatternResponse.class)))
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseData<String> createPattern(
            @RequestBody PatternRequest request) {
        patternService.createOrUpdate(request);
        return ResponseData.<String>builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message(MessageConstant.MSG_CREATE_OR_UPDATE_SUCCESS)
                .build();
    }

    @Operation(summary = "Get paginated list of patterns")
    @ApiResponse(responseCode = "200", description = "List of patterns",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PaginationResponse.class)))
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    public ResponseData<PaginationResponse<PatternResponse>> getPatterns(
            @Parameter(description = "Page number (default: 0)")
            @RequestParam(value = "offset", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER,
                    required = false) int offset,
            @Parameter(description = "Page size (default: 48)")
            @RequestParam(value = "limit", defaultValue = AppConstant.DEFAULT_PAGE_SIZE,
                    required = false) int limit,
            @Parameter(description = "Sort by field (default: createdDate)")
            @RequestParam(value = "sortBy", defaultValue = AppConstant.DEFAULT_SORT_BY, required = false) String sortBy,
            @Parameter(description = "Sort direction (default: DESC)")
            @RequestParam(value = "sortDir", defaultValue = AppConstant.DEFAULT_SORT_DIRECTION,
                    required = false) String sortDir,
            @Parameter(description = "List filters")
            @RequestBody(required = false) Filter[] filters) {
        var response = patternService.getPatterns(offset, limit, sortBy, sortDir, filters);
        return ResponseData.<PaginationResponse<PatternResponse>>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SUCCESS)
                .data(response)
                .build();
    }

    @Operation(summary = "Get pattern details by ID")
    @ApiResponse(responseCode = "200", description = "Pattern details retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PatternResponse.class)))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ResponseData<PatternResponse> getDetail(
            @Parameter(description = "ID of the pattern to retrieve")
            @PathVariable("id") String id) {
        var res = patternService.getDetail(id);
        return ResponseData.<PatternResponse>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SUCCESS)
                .data(res)
                .build();
    }

    @Operation(summary = "Delete a pattern")
    @ApiResponse(responseCode = "200", description = "Pattern deleted successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "Pattern not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseData<String> deletePattern(
            @Parameter(description = "ID of the pattern to delete")
            @RequestParam("id") String id) {
        patternService.deletePattern(id);
        return ResponseData.<String>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(MessageConstant.MSG_DELETE_SUCCESS)
                .build();
    }

    @Operation(summary = "Get pattern ids")
    @ApiResponse(responseCode = "200", description = "List of pattern ids",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = List.class)))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/ids")
    public ResponseData<List<String>> getPatternIds(@RequestParam("offset") int offset, @RequestParam("limit") int limit) {
        var res = patternService.getPatternIds(offset, limit);
        return ResponseData.<List<String>>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SUCCESS)
                .data(res)
                .build();
    }
}
