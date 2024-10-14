package org.crochet.service;

import org.crochet.payload.request.CollectionRequest;
import org.crochet.payload.response.CollectionResponse;
import org.crochet.security.UserPrincipal;

import java.util.List;

public interface CollectionService {
    CollectionResponse saveCollection(CollectionRequest request, UserPrincipal principal);
    List<CollectionResponse> getAllCollections();
    List<CollectionResponse> getAllCollectionsByUserId(String userId);
    CollectionResponse getCollectionById(String id);
    void deleteCollection(String id);
}
