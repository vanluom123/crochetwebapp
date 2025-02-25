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
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.payload.response.PaginationResponse;
import org.crochet.payload.response.ResponseData;
import org.crochet.service.FreePatternService;
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
@RequestMapping("/api/v1/free-pattern")
public class FreePatternController {
    private final FreePatternService freePatternService;

    public FreePatternController(FreePatternService freePatternService) {
        this.freePatternService = freePatternService;
    }

    @Operation(summary = "Create a free pattern")
    @ApiResponse(responseCode = "201", description = "Free Pattern created successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = FreePatternResponse.class)))
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseData<String> createPattern(@RequestBody FreePatternRequest request) {
        freePatternService.createOrUpdate(request);
        return ResponseData.<String>builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message(MessageConstant.MSG_CREATE_OR_UPDATE_SUCCESS)
                .build();
    }

    @Operation(summary = "Get pattern details by ID")
    @ApiResponse(responseCode = "200", description = "Free Pattern details retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = FreePatternResponse.class)))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ResponseData<FreePatternResponse> getDetail(
            @Parameter(description = "ID of the pattern to retrieve")
            @PathVariable("id") String id) {
        var response = freePatternService.getDetail(id);
        return ResponseData.<FreePatternResponse>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SUCCESS)
                .data(response)
                .build();
    }

    @Operation(summary = "Delete a free pattern")
    @ApiResponse(responseCode = "200", description = "Free pattern deleted successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseData<String> delete(
            @Parameter(description = "ID of the pattern to delete")
            @RequestParam("id") String id) {
        freePatternService.delete(id);
        return ResponseData.<String>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Delete pattern successfully")
                .build();
    }

    @Operation(summary = "Delete free patterns by ids")
    @ApiResponse(responseCode = "200", description = "Free patterns deleted successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseData.class)))
    @DeleteMapping("/delete-multiple")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseData<String> deleteMultiple(
            @Parameter(description = "List of pattern ids to delete")
            @RequestBody List<String> ids) {
        freePatternService.deleteAllById(ids);
        return ResponseData.<String>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Delete patterns successfully")
                .build();
    }

    @Operation(summary = "Get paginated list of patterns")
    @ApiResponse(responseCode = "200", description = "List of free patterns",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PaginationResponse.class)))
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    public ResponseData<PaginationResponse<FreePatternResponse>> getAllFreePatterns(
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
        var response = freePatternService.getAllFreePatterns(offset, limit, sortBy, sortDir, filters);
        return ResponseData.<PaginationResponse<FreePatternResponse>>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SUCCESS)
                .data(response)
                .build();
    }

    @Operation(summary = "Get free pattern ids")
    @ApiResponse(responseCode = "200", description = "List of free pattern ids",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = List.class)))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/ids")
    public ResponseData<List<String>> getFreePatternIds(@RequestParam("offset") int offset,
                                                        @RequestParam("limit") int limit) {
        var res = freePatternService.getFreePatternIds(offset, limit);
        return ResponseData.<List<String>>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SUCCESS)
                .data(res)
                .build();
    }
}
