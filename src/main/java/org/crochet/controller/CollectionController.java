package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.crochet.enums.ResultCode;
import org.crochet.payload.response.ResponseData;
import org.crochet.service.CollectionService;
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
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class CollectionController {
    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
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
    @PostMapping("/{collection_id}/add-pattern/{free_pattern_id}")
    public ResponseData<String> addPatternToCollection(
            @Parameter(description = "Collection ID")
            @PathVariable("collection_id") String collectionId,
            @Parameter(description = "Free pattern ID")
            @PathVariable("free_pattern_id") String freePatternId) {
        collectionService.addFreePatternToCollection(collectionId, freePatternId);
        return ResponseUtil.success(ResultCode.MSG_CREATE_OR_UPDATE_SUCCESS.message());
    }

    @Operation(summary = "Update a collection")
    @ApiResponse(responseCode = "200", description = "Collection updated successfully",
            content = @Content(mediaType = "application/json"))
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{collectionId}")
    public ResponseData<String> updateCollection(
            @Parameter(description = "Collection ID")
            @PathVariable("collectionId") String collectionId,
            @Parameter(description = "Name")
            @RequestParam("name") String name) {
        collectionService.updateCollection(collectionId, name);
        return ResponseUtil.success(ResultCode.MSG_CREATE_OR_UPDATE_SUCCESS.message());
    }

    @Operation(summary = "Remove a free pattern from collection")
    @ApiResponse(responseCode = "204", description = "Pattern removed from collection successfully")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/remove-pattern/{free_pattern_id}")
    public ResponseData<String> removePatternFromCollection(
            @Parameter(description = "Free pattern ID")
            @PathVariable("free_pattern_id") String freePatternId) {
        collectionService.removeFreePatternFromCollection(freePatternId);
        return ResponseUtil.success(ResultCode.MSG_DELETE_SUCCESS.message());
    }

    @Operation(summary = "Delete a collection")
    @ApiResponse(responseCode = "204", description = "Collection deleted successfully")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{collection_id}")
    public ResponseData<String> deleteCollection(
            @Parameter(description = "Collection ID") @PathVariable("collection_id") String collectionId) {
        collectionService.deleteCollection(collectionId);
        return ResponseUtil.success(ResultCode.MSG_DELETE_SUCCESS.message());
    }

    @Operation(summary = "Check if a free pattern exists in user's collections")
    @ApiResponse(responseCode = "200", description = "Returns true if pattern exists in any collection",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseData.class)))
    @GetMapping("/{free_pattern_id}/exists")
    public ResponseData<Boolean> checkFreePatternInCollection(
            @Parameter(description = "Free pattern ID")
            @PathVariable("free_pattern_id") String freePatternId) {
        var res = collectionService.checkFreePatternInCollection(freePatternId);
        return ResponseUtil.success(res);
    }
}