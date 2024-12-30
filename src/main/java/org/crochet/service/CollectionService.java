package org.crochet.service;

import java.util.List;

import org.crochet.payload.request.UpdateCollectionRequest;
import org.crochet.payload.response.CollectionResponse;
import org.crochet.payload.response.FreePatternOnHome;

public interface CollectionService {
    String addFreePatternToCollection(String collectionId, String freePatternId);

    String createCollection(String name);

    String updateCollection(String collectionId, UpdateCollectionRequest request);

    void removeFreePatternFromCollection(String freePatternId);

    List<CollectionResponse> getUserCollections();

    CollectionResponse getCollectionById(String collectionId);

    void deleteCollection(String collectionId);

    List<FreePatternOnHome> getFreePatternsInCollection(String collectionId);
}
