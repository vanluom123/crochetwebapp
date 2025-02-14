package org.crochet.service.impl;

import lombok.RequiredArgsConstructor;
import org.crochet.enums.ResultCode;
import org.crochet.exception.AccessDeniedException;
import org.crochet.exception.BadRequestException;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.ColFrep;
import org.crochet.model.Collection;
import org.crochet.model.FreePattern;
import org.crochet.payload.response.CollectionResponse;
import org.crochet.repository.ColFrepRepo;
import org.crochet.repository.CollectionRepo;
import org.crochet.repository.FreePatternRepository;
import org.crochet.service.CollectionAvatarService;
import org.crochet.service.CollectionService;
import org.crochet.util.ObjectUtils;
import org.crochet.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CollectionServiceImpl implements CollectionService {
    private final CollectionRepo collectionRepo;
    private final FreePatternRepository freePatternRepository;
    private final ColFrepRepo colFrepRepo;
    private final CollectionAvatarService avatarService;

    /**
     * Add a free pattern to a collection
     *
     * @param collectionId  collection id
     * @param freePatternId free pattern id
     */
    @Override
    public void addFreePatternToCollection(String collectionId, String freePatternId) {
        var collection = collectionRepo.findColById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ResultCode.MSG_COLLECTION_NOT_FOUND.message(),
                        ResultCode.MSG_COLLECTION_NOT_FOUND.code()
                ));

        FreePattern freePattern = freePatternRepository.findFrepById(freePatternId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ResultCode.MSG_FREE_PATTERN_NOT_FOUND.message(),
                        ResultCode.MSG_FREE_PATTERN_NOT_FOUND.code()
                ));

        addNewPatternToCollection(collection, freePattern);
        updateCollectionAvatarIfFirst(collection, freePattern);
    }

    /**
     * Creates a new collection with the specified name for the current user.
     *
     * @param name the name of the collection to be created
     * @throws BadRequestException       if a collection with the given name already
     *                                   exists
     * @throws ResourceNotFoundException if the user associated with the current
     *                                   session cannot be found
     */
    @Override
    public void createCollection(String name) {
        var user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new ResourceNotFoundException(
                    ResultCode.MSG_USER_NOT_FOUND.message(),
                    ResultCode.MSG_USER_NOT_FOUND.code()
            );
        }

        if (collectionRepo.existsCollectionByName(user.getId(), name)) {
            throw new BadRequestException("Collection name already exists");
        }

        Collection collection = new Collection();
        collection.setName(name);
        collection.setUser(user);

        collectionRepo.save(collection);
    }

    /**
     * Update a collection
     *
     * @param collectionId collection id
     * @param name         update collection request
     */
    @Override
    public void updateCollection(String collectionId, String name) {
        var user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new ResourceNotFoundException(
                    ResultCode.MSG_USER_NOT_FOUND.message(),
                    ResultCode.MSG_USER_NOT_FOUND.code()
            );
        }

        var col = collectionRepo.findColById(collectionId)
                .orElseThrow(() -> new AccessDeniedException(
                        ResultCode.MSG_NO_PERMISSION_MODIFY_COLLECTION.message(),
                        ResultCode.MSG_NO_PERMISSION_MODIFY_COLLECTION.code()
                ));

        if (collectionRepo.existsCollectionByName(user.getId(), name)) {
            throw new BadRequestException("Collection name already exists");
        }

        col.setName(name);
        collectionRepo.save(col);
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
            throw new ResourceNotFoundException(
                    ResultCode.MSG_USER_NOT_FOUND.message(),
                    ResultCode.MSG_USER_NOT_FOUND.code()
            );
        }
        var freePattern = freePatternRepository.findFrepById(freePatternId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ResultCode.MSG_FREE_PATTERN_NOT_FOUND.message(),
                        ResultCode.MSG_FREE_PATTERN_NOT_FOUND.code()
                ));
        if (ObjectUtils.notEqual(freePattern.getCreatedBy(), user.getId())) {
            throw new AccessDeniedException(
                    ResultCode.MSG_NO_PERMISSION_REMOVE_FREE_PATTERN_FROM_COLLECTION.message(),
                    ResultCode.MSG_NO_PERMISSION_REMOVE_FREE_PATTERN_FROM_COLLECTION.code()
            );
        }
        var collection = colFrepRepo.findColByUserAndFreePattern(user.getId(), freePatternId);
        colFrepRepo.removeByFreePattern(freePatternId);
        avatarService.updateAvatarFromNextPattern(collection);
    }

    /**
     * Get a collection by id
     *
     * @param collectionId collection id
     * @return collection
     */
    @Override
    public CollectionResponse getCollectionById(String collectionId) {
        return collectionRepo.getColById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ResultCode.MSG_COLLECTION_NOT_FOUND.message(),
                        ResultCode.MSG_COLLECTION_NOT_FOUND.code()
                ));
    }

    @Override
    public List<CollectionResponse> getAllByUserId(String userId) {
        return collectionRepo.getAllByUserId(userId);
    }

    /**
     * Deletes a collection associated with the specified collection ID for the currently logged-in user.
     * Ensures the user has the necessary permissions to delete the requested collection.
     *
     * @param collectionId the unique identifier of the collection to be deleted
     * @throws ResourceNotFoundException if the current user cannot be retrieved
     * @throws AccessDeniedException     if the user does not have permission to modify the specified collection
     */
    @Override
    public void deleteCollection(String collectionId) {
        var user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new ResourceNotFoundException(
                    ResultCode.MSG_USER_NOT_FOUND.message(),
                    ResultCode.MSG_USER_NOT_FOUND.code()
            );
        }

        var col = collectionRepo.findColById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ResultCode.MSG_COLLECTION_NOT_FOUND.message(),
                        ResultCode.MSG_COLLECTION_NOT_FOUND.code()
                ));

        if (ObjectUtils.notEqual(col.getUser().getId(), user.getId())) {
            throw new AccessDeniedException(
                    ResultCode.MSG_NO_PERMISSION_DELETE_COLLECTION.message(),
                    ResultCode.MSG_NO_PERMISSION_DELETE_COLLECTION.code()
            );
        }

        collectionRepo.delete(col);
    }

    /**
     * Add a new pattern to a collection
     *
     * @param collection  Collection
     * @param freePattern FreePattern
     */
    private void addNewPatternToCollection(Collection collection, FreePattern freePattern) {
        ColFrep colFrep = new ColFrep();
        colFrep.setCollection(collection);
        colFrep.setFreePattern(freePattern);
        colFrepRepo.save(colFrep);
    }

    /**
     * Update collection avatar if it is the first pattern in the collection
     *
     * @param collection  Collection
     * @param freePattern FreePattern
     */
    private void updateCollectionAvatarIfFirst(Collection collection, FreePattern freePattern) {
        long count = colFrepRepo.countByCollectionId(collection.getId());
        if (count == 1) {
            avatarService.updateAvatar(collection, freePattern);
        }
    }
}