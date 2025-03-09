package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.crochet.constant.AppConstant;
import org.crochet.enums.ResultCode;
import org.crochet.payload.response.CollectionResponse;
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.payload.response.PaginationResponse;
import org.crochet.payload.response.ResponseData;
import org.crochet.service.CollectionService;
import org.crochet.service.FreePatternService;
import org.crochet.util.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/collections")
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("isAuthenticated()")
public class CollectionController {
    private final CollectionService collectionService;
    private final FreePatternService freePatternService;

    public CollectionController(CollectionService collectionService,
                                FreePatternService freePatternService) {
        this.collectionService = collectionService;
        this.freePatternService = freePatternService;
    }

    @Operation(summary = "Create a new collection")
    @ApiResponse(responseCode = "201", description = "Collection created successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseData<String> create(@RequestParam("name") String name) {
        collectionService.createCollection(name);
        return ResponseUtil.success(ResultCode.MSG_CREATE_OR_UPDATE_SUCCESS.message());
    }

    @Operation(summary = "Add a free pattern to collection")
    @ApiResponse(responseCode = "200", description = "Pattern added to collection successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/add-pattern")
    public ResponseData<String> addPatternToCollection(
            @Parameter(description = "Collection ID")
            @RequestParam("collection_id") String collectionId,
            @Parameter(description = "Free pattern ID")
            @RequestParam("free_pattern_id") String freePatternId) {
        collectionService.addFreePatternToCollection(collectionId, freePatternId);
        return ResponseUtil.success(ResultCode.MSG_CREATE_OR_UPDATE_SUCCESS.message());
    }

    @Operation(summary = "Update a collection")
    @ApiResponse(responseCode = "200", description = "Collection updated successfully",
            content = @Content(mediaType = "application/json"))
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public ResponseData<String> updateCollection(
            @Parameter(description = "Collection ID")
            @RequestParam("collectionId") String collectionId,
            @Parameter(description = "Name")
            @RequestParam("name") String name) {
        collectionService.updateCollection(collectionId, name);
        return ResponseUtil.success(ResultCode.MSG_CREATE_OR_UPDATE_SUCCESS.message());
    }

    @Operation(summary = "Remove a free pattern from collection")
    @ApiResponse(responseCode = "204", description = "Pattern removed from collection successfully")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/remove-pattern")
    public ResponseData<String> removePatternFromCollection(
            @Parameter(description = "Free pattern ID")
            @RequestParam("free_pattern_id") String freePatternId) {
        collectionService.removeFreePatternFromCollection(freePatternId);
        return ResponseUtil.success(ResultCode.MSG_DELETE_SUCCESS.message());
    }

    @Operation(summary = "Get collection by ID")
    @ApiResponse(responseCode = "200", description = "Collection details",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CollectionResponse.class)))
    @GetMapping("/{collection_id}")
    public ResponseData<CollectionResponse> getCollectionById(
            @Parameter(description = "Collection ID") @PathVariable("collection_id") String collectionId) {
        var res = collectionService.getCollectionById(collectionId);
        return ResponseUtil.success(res);
    }

    @GetMapping("/{collection_id}/free-patterns")
    public ResponseData<PaginationResponse<FreePatternResponse>> getAllByCollection(
            @Parameter(description = "Page number (default: 0)")
            @RequestParam(value = "pageNo", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER,
                    required = false) int pageNo,
            @Parameter(description = "Page size (default: 48)")
            @RequestParam(value = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE,
                    required = false) int pageSize,
            @Parameter(description = "Sort by field (default: createdDate)")
            @RequestParam(value = "sortBy", defaultValue = AppConstant.DEFAULT_SORT_BY, required = false) String sortBy,
            @Parameter(description = "Sort direction (default: DESC)")
            @RequestParam(value = "sortDir", defaultValue = AppConstant.DEFAULT_SORT_DIRECTION,
                    required = false) String sortDir,
            @PathVariable("collection_id") String collectionId) {
        var response = freePatternService.getFrepsByCollectionId(collectionId, pageNo, pageSize, sortBy, sortDir);
        return ResponseUtil.success(response);
    }

    @Operation(summary = "Delete a collection")
    @ApiResponse(responseCode = "204", description = "Collection deleted successfully")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{collectionId}")
    public ResponseData<String> deleteCollection(
            @Parameter(description = "Collection ID") @PathVariable("collectionId") String collectionId) {
        collectionService.deleteCollection(collectionId);
        return ResponseUtil.success(ResultCode.MSG_DELETE_SUCCESS.message());
    }
}