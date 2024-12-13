package org.crochet.service.impl;

import lombok.RequiredArgsConstructor;
import org.crochet.exception.AccessDeniedException;
import org.crochet.exception.BadRequestException;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.ColFrep;
import org.crochet.model.Collection;
import org.crochet.model.FreePattern;
import org.crochet.payload.request.UpdateCollectionRequest;
import org.crochet.payload.response.CollectionResponse;
import org.crochet.repository.ColFrepRepo;
import org.crochet.repository.CollectionRepo;
import org.crochet.repository.FreePatternRepository;
import org.crochet.repository.UserRepository;
import org.crochet.service.CollectionService;
import org.crochet.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.crochet.constant.MessageCodeConstant.MAP_CODE;
import static org.crochet.constant.MessageConstant.MSG_COLLECTION_NOT_FOUND;
import static org.crochet.constant.MessageConstant.MSG_FREE_PATTERN_NOT_FOUND;
import static org.crochet.constant.MessageConstant.MSG_NOT_AUTHENTICATED;
import static org.crochet.constant.MessageConstant.MSG_NO_PERMISSION_MODIFY_COLLECTION;
import static org.crochet.constant.MessageConstant.MSG_USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class CollectionServiceImpl implements CollectionService {
    private final CollectionRepo collectionRepo;
    private final UserRepository userRepository;
    private final FreePatternRepository freePatternRepository;
    private final ColFrepRepo colFrepRepo;

    /**
     * Add a free pattern to a collection
     *
     * @param collectionId  collection id
     * @param freePatternId free pattern id
     * @return CollectionResponse
     */
    @Override
    public String addFreePatternToCollection(String collectionId, String freePatternId) {
        var user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new AccessDeniedException(MSG_NOT_AUTHENTICATED,
                    MAP_CODE.get(MSG_NOT_AUTHENTICATED));
        }

        var collection = collectionRepo.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_COLLECTION_NOT_FOUND,
                        MAP_CODE.get(MSG_COLLECTION_NOT_FOUND)));

        FreePattern freePattern = freePatternRepository.findById(freePatternId)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_FREE_PATTERN_NOT_FOUND,
                        MAP_CODE.get(MSG_FREE_PATTERN_NOT_FOUND)));

        ColFrep colFrep = new ColFrep();
        colFrep.setCollection(collection);
        colFrep.setFreePattern(freePattern);
        colFrepRepo.save(colFrep);

        return "Add success";
    }

    /**
     * Creates a new collection with the specified name for the current user.
     *
     * @param name the name of the collection to be created
     * @return a success message indicating the collection was created successfully
     * @throws AccessDeniedException if the current user is not authenticated
     * @throws BadRequestException if a collection with the given name already exists
     * @throws ResourceNotFoundException if the user associated with the current session cannot be found
     */
    @Override
    public String createCollection(String name) {
        var user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new AccessDeniedException(MSG_NOT_AUTHENTICATED,
                    MAP_CODE.get(MSG_NOT_AUTHENTICATED));
        }

        if (collectionRepo.existsCollectionByName(name)) {
            throw new BadRequestException("Collection name already exists");
        }

        Collection collection = new Collection();
        collection.setName(name);
        collection.setUser(userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(MSG_USER_NOT_FOUND,
                        MAP_CODE.get(MSG_USER_NOT_FOUND))));

        collectionRepo.save(collection);

        return "Create success";
    }

    /**
     * Update a collection
     *
     * @param collectionId collection id
     * @param request      update collection request
     * @return collection
     */
    @Override
    public String updateCollection(String collectionId, UpdateCollectionRequest request) {
        var user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new AccessDeniedException(MSG_NOT_AUTHENTICATED,
                    MAP_CODE.get(MSG_NOT_AUTHENTICATED));
        }

        var res = collectionRepo.getCollectionByUserId(collectionId, user.getId())
                .orElseThrow(() -> new AccessDeniedException(MSG_NO_PERMISSION_MODIFY_COLLECTION,
                        MAP_CODE.get(MSG_NO_PERMISSION_MODIFY_COLLECTION)));

        if (res.getName().equals(request.getName())) {
            throw new BadRequestException("Collection name already exists");
        }

        Collection collection = new Collection();
        collection.setName(request.getName());

        collectionRepo.save(collection);

        return "Update success";
    }

    /**
     * Remove a free pattern from a collection
     *
     * @param freePatternId free pattern id
     */
    @Override
    public void removeFreePatternFromCollection(String freePatternId) {
        var user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new AccessDeniedException(MSG_NOT_AUTHENTICATED,
                    MAP_CODE.get(MSG_NOT_AUTHENTICATED));
        }

        var frep = freePatternRepository.findFrepInCollection(user.getId(), freePatternId)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_FREE_PATTERN_NOT_FOUND,
                        MAP_CODE.get(MSG_FREE_PATTERN_NOT_FOUND)));

        colFrepRepo.removeByFreePattern(frep);
    }

    /**
     * Get all collections of a user
     *
     * @return list of collections
     */
    @Override
    public List<CollectionResponse> getUserCollections() {
        var user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new AccessDeniedException(MSG_NOT_AUTHENTICATED,
                    MAP_CODE.get(MSG_NOT_AUTHENTICATED));
        }

        return collectionRepo.getCollectionsByUserId(user.getId());
    }

    /**
     * Get a collection by id
     *
     * @param collectionId collection id
     * @return collection
     */
    @Override
    public CollectionResponse getCollectionById(String collectionId) {
        var user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new AccessDeniedException(MSG_NOT_AUTHENTICATED,
                    MAP_CODE.get(MSG_NOT_AUTHENTICATED));
        }

        return collectionRepo.getCollectionByUserId(collectionId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(MSG_COLLECTION_NOT_FOUND,
                        MAP_CODE.get(MSG_COLLECTION_NOT_FOUND)));
    }

    /**
     * Deletes a collection with the given id, if the current user has permission
     * to do so.
     *
     * @param collectionId the id of the collection to be deleted
     * @throws AccessDeniedException if the current user is not authenticated
     * @throws AccessDeniedException if the current user does not have permission
     * to modify the collection
     */
    @Override
    public void deleteCollection(String collectionId) {
        var user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new AccessDeniedException(MSG_NOT_AUTHENTICATED,
                    MAP_CODE.get(MSG_NOT_AUTHENTICATED));
        }

        var res = collectionRepo.getCollectionByUserId(collectionId, user.getId())
                .orElseThrow(() -> new AccessDeniedException(MSG_NO_PERMISSION_MODIFY_COLLECTION,
                        MAP_CODE.get(MSG_NO_PERMISSION_MODIFY_COLLECTION)));

        Collection col = new Collection();
        col.setName(res.getName());
        col.setId(res.getId());

        collectionRepo.delete(col);
    }
}