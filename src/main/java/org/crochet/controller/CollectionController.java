package org.crochet.controller;

import org.crochet.payload.request.CollectionRequest;
import org.crochet.payload.response.CollectionResponse;
import org.crochet.security.CurrentUser;
import org.crochet.security.UserPrincipal;
import org.crochet.service.CollectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {

    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @PostMapping("/createOrUpdate")
    public ResponseEntity<CollectionResponse> createCollection(@RequestBody CollectionRequest request,
                                                               @CurrentUser UserPrincipal principal) {
        CollectionResponse response = collectionService.saveCollection(request, principal);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CollectionResponse>> getAllCollections() {
        List<CollectionResponse> collections = collectionService.getAllCollections();
        return ResponseEntity.ok(collections);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CollectionResponse>> getCollectionsByUserId(@PathVariable String userId) {
        List<CollectionResponse> collections = collectionService.getAllCollectionsByUserId(userId);
        return ResponseEntity.ok(collections);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CollectionResponse> getCollectionById(@PathVariable String id) {
        CollectionResponse response = collectionService.getCollectionById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollection(@PathVariable String id) {
        collectionService.deleteCollection(id);
        return ResponseEntity.noContent().build();
    }
}
