package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.crochet.constant.AppConstant;
import org.crochet.payload.request.PaginationRequest;
import org.crochet.payload.response.CollectionResponse;
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.payload.response.PaginationResponse;
import org.crochet.payload.response.ResponseData;
import org.crochet.service.CollectionService;
import org.crochet.service.FreePatternService;
import org.springframework.data.domain.Sort;
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

import static org.crochet.constant.AppConstant.SUCCESS;

@RestController
@RequestMapping("/collections")
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class CollectionController {
    private final CollectionService collectionService;
    private final FreePatternService freePatternService;

    public CollectionController(CollectionService collectionService,
                                FreePatternService freePatternService) {
        this.collectionService = collectionService;
        this.freePatternService = freePatternService;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new collection")
    @ApiResponse(responseCode = "201", description = "Collection created successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    @PostMapping(value = "/create")
    public ResponseData<String> create(@RequestParam("name") String name) {
        collectionService.createCollection(name);
        return ResponseData.<String>builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message(SUCCESS)
                .data("Create success")
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
        collectionService.addFreePatternToCollection(collectionId, freePatternId);
        return ResponseData.<String>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SUCCESS)
                .data("Added free pattern")
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a collection")
    @ApiResponse(responseCode = "200", description = "Collection updated successfully",
            content = @Content(mediaType = "application/json"))
    @PutMapping("/update")
    public ResponseData<String> updateCollection(
            @Parameter(description = "Collection ID")
            @RequestParam("collectionId") String collectionId,
            @Parameter(description = "Name")
            @RequestParam("name") String name) {
        collectionService.updateCollection(collectionId, name);
        return ResponseData.<String>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SUCCESS)
                .data("Update success")
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
                .data("Removed free pattern from collection")
                .build();
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

    @GetMapping("/{collection_id}/free-patterns")
    public ResponseEntity<PaginationResponse<FreePatternResponse>> getAllByCollection(
            @Parameter(description = "Page number (default: 0)")
            @RequestParam(value = "pageNo", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER,
                    required = false) int offset,
            @Parameter(description = "Page size (default: 48)")
            @RequestParam(value = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE,
                    required = false) int limit,
            @Parameter(description = "Sort by field (default: createdDate)")
            @RequestParam(value = "sortBy", defaultValue = AppConstant.DEFAULT_SORT_BY, required = false) String orderBy,
            @Parameter(description = "Sort direction (default: DESC)")
            @RequestParam(value = "sortDir", defaultValue = AppConstant.DEFAULT_SORT_DIRECTION,
                    required = false) String direction,
            @PathVariable("collection_id") String collectionId) {
        PaginationRequest request = PaginationRequest.builder()
                .offset(offset)
                .limit(limit)
                .orderBy(orderBy)
                .direction(Sort.Direction.fromString(direction))
                .build();
        var response = freePatternService.getFrepsByCollectionId(collectionId, request);
        return ResponseEntity.ok(response);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a collection")
    @ApiResponse(responseCode = "204", description = "Collection deleted successfully")
    @DeleteMapping("/delete")
    public ResponseData<String> deleteCollection(
            @Parameter(description = "Collection ID") @RequestParam("collectionId") String collectionId) {
        collectionService.deleteCollection(collectionId);
        return ResponseData.<String>builder()
                .success(true)
                .code(HttpStatus.NO_CONTENT.value())
                .message(SUCCESS)
                .data("Delete success")
                .build();
    }
}