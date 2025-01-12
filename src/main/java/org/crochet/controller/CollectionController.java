package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.crochet.payload.request.UpdateCollectionRequest;
import org.crochet.payload.response.CollectionResponse;
import org.crochet.payload.response.FreePatternOnHome;
import org.crochet.payload.response.ResponseData;
import org.crochet.service.CollectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.crochet.constant.AppConstant.SUCCESS;

@RestController
@RequestMapping("/collections")
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class CollectionController {
    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new collection")
    @ApiResponse(responseCode = "201", description = "Collection created successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    @PostMapping(value = "/create")
    public ResponseData<String> create(@RequestParam("name") String name) {
        var collection = collectionService.createCollection(name);
        return ResponseData.<String>builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message(SUCCESS)
                .data(collection)
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Add a free pattern to collection")
    @ApiResponse(responseCode = "200", description = "Pattern added to collection successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    @PostMapping("/add-pattern")
    public ResponseData<String> addPatternToCollection(
            @Parameter(description = "Collection ID")
            @RequestParam("collection_id") String collectionId,
            @Parameter(description = "Free pattern ID")
            @RequestParam("free_pattern_id") String freePatternId) {
        var result = collectionService.addFreePatternToCollection(collectionId, freePatternId);
        return ResponseData.<String>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SUCCESS)
                .data(result)
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a collection")
    @ApiResponse(responseCode = "200", description = "Collection updated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    @PutMapping("/update/{collectionId}")
    public ResponseData<String> updateCollection(
            @Parameter(description = "Collection ID") @PathVariable("collectionId") String collectionId,
            @RequestBody UpdateCollectionRequest request) {
        var collection = collectionService.updateCollection(collectionId, request);
        return ResponseData.<String>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SUCCESS)
                .data(collection)
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove a free pattern from collection")
    @ApiResponse(responseCode = "204", description = "Pattern removed from collection successfully")
    @DeleteMapping("/remove-pattern")
    public ResponseData<String> removePatternFromCollection(
            @Parameter(description = "Free pattern ID")
            @RequestParam("free_pattern_id") String freePatternId) {
        collectionService.removeFreePatternFromCollection(freePatternId);
        return ResponseData.<String>builder()
                .success(true)
                .code(HttpStatus.NO_CONTENT.value())
                .message(SUCCESS)
                .data("Remove free pattern from collection successfully")
                .build();
    }

    @Operation(summary = "Get user's collections")
    @ApiResponse(responseCode = "200", description = "List of user's collections",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CollectionResponse.class)))
    @GetMapping("/my-collections")
    public ResponseEntity<List<CollectionResponse>> getUserCollections() {
        var collections = collectionService.getUserCollections();
        return ResponseEntity.ok(collections);
    }

    @Operation(summary = "Get collection by ID")
    @ApiResponse(responseCode = "200", description = "Collection details",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CollectionResponse.class)))
    @GetMapping("/{collectionId}")
    public ResponseEntity<CollectionResponse> getCollectionById(
            @Parameter(description = "Collection ID") @PathVariable("collectionId") String collectionId) {
        var collection = collectionService.getCollectionById(collectionId);
        return ResponseEntity.ok(collection);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a collection")
    @ApiResponse(responseCode = "204", description = "Collection deleted successfully")
    @DeleteMapping("/delete/{collectionId}")
    public ResponseData<String> deleteCollection(
            @Parameter(description = "Collection ID") @PathVariable("collectionId") String collectionId) {
        collectionService.deleteCollection(collectionId);
        return ResponseData.<String>builder()
                .success(true)
                .code(HttpStatus.NO_CONTENT.value())
                .message(SUCCESS)
                .data("Delete collection successfully")
                .build();
    }

    @Operation(summary = "Get all free patterns in a collection")
    @ApiResponse(responseCode = "200", description = "List of free patterns in collection",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CollectionResponse.class)))
    @GetMapping("/{collectionId}/free-patterns")
    public ResponseEntity<List<FreePatternOnHome>> getFreePatternsInCollection(
            @Parameter(description = "Collection ID") @PathVariable("collectionId") String collectionId) {
        var freePatterns = collectionService.getFreePatternsInCollection(collectionId);
        return ResponseEntity.ok(freePatterns);
    }
}