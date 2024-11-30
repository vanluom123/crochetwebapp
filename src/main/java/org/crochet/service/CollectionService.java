package org.crochet.service;

import java.util.List;

import org.crochet.payload.request.CreateCollectionRequest;
import org.crochet.payload.request.UpdateCollectionRequest;
import org.crochet.payload.response.CollectionResponse;

public interface CollectionService {
    String addFreePatternToCollection(String collectionId, String freePatternId);
    String createCollection(CreateCollectionRequest request);
    String updateCollection(String collectionId, UpdateCollectionRequest request);
    void removeFreePatternFromCollection(String freePatternId);
    List<CollectionResponse> getUserCollections();
    CollectionResponse getCollectionById(String collectionId);
    void deleteCollection(String collectionId);
}
