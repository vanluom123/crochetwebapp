package org.crochet.service;

import org.crochet.payload.response.CollectionResponse;

import java.util.List;

public interface CollectionService {
    void addFreePatternToCollection(String collectionId, String freePatternId);

    void createCollection(String name);

    void updateCollection(String collectionId, String name);

    void removeFreePatternFromCollection(String freePatternId);

    CollectionResponse getCollectionById(String collectionId);

    List<CollectionResponse> getAllByUserId(String userId);

    void deleteCollection(String collectionId);
}
