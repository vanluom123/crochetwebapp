package org.crochet.service;

import org.crochet.payload.request.CollectionRequest;
import org.crochet.payload.response.CollectionResponse;

import java.util.List;

public interface CollectionService {
    CollectionResponse saveCollection(CollectionRequest request);
    List<CollectionResponse> getAllCollections();
    List<CollectionResponse> getAllCollectionsByUserId(String userId);
    CollectionResponse getCollectionById(String id);
    void deleteCollection(String id);
}
