package org.crochet.service.impl;

import lombok.RequiredArgsConstructor;
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
import org.crochet.service.CollectionService;
import org.crochet.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static org.crochet.constant.MessageCodeConstant.MAP_CODE;
import static org.crochet.constant.MessageConstant.MSG_COLLECTION_NOT_FOUND;
import static org.crochet.constant.MessageConstant.MSG_FREE_PATTERN_NOT_FOUND;
import static org.crochet.constant.MessageConstant.MSG_NO_PERMISSION_MODIFY_COLLECTION;
import static org.crochet.constant.MessageConstant.MSG_USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class CollectionServiceImpl implements CollectionService {
    private final CollectionRepo collectionRepo;
    private final FreePatternRepository freePatternRepository;
    private final ColFrepRepo colFrepRepo;

    /**
     * Add a free pattern to a collection
     *
     * @param collectionId  collection id
     * @param freePatternId free pattern id
     */
    @Override
    public void addFreePatternToCollection(String collectionId, String freePatternId) {
        var collection = collectionRepo.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_COLLECTION_NOT_FOUND,
                        MAP_CODE.get(MSG_COLLECTION_NOT_FOUND)));

        FreePattern freePattern = freePatternRepository.findFrepById(freePatternId)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_FREE_PATTERN_NOT_FOUND,
                        MAP_CODE.get(MSG_FREE_PATTERN_NOT_FOUND)));

        ColFrep colFrep = new ColFrep();
        colFrep.setCollection(collection);
        colFrep.setFreePattern(freePattern);
        colFrepRepo.save(colFrep);

        long count = colFrepRepo.countByCollectionId(collectionId);
        if (count == 1) {
            var images = freePattern.getImages();
            if (!images.isEmpty()) {
                collection.setAvatar(images.getFirst().getFileContent());
            } else {
                collection.setAvatar(null);
            }
            collectionRepo.save(collection);
        }
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
            throw new ResourceNotFoundException(MSG_USER_NOT_FOUND,
                    MAP_CODE.get(MSG_USER_NOT_FOUND));
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
            throw new ResourceNotFoundException(MSG_USER_NOT_FOUND,
                    MAP_CODE.get(MSG_USER_NOT_FOUND));
        }

        var col = collectionRepo.findColById(collectionId)
                .orElseThrow(() -> new AccessDeniedException(MSG_NO_PERMISSION_MODIFY_COLLECTION,
                        MAP_CODE.get(MSG_NO_PERMISSION_MODIFY_COLLECTION)));

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
        colFrepRepo.removeByFreePattern(freePatternId);
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
                .orElseThrow(() -> new ResourceNotFoundException(MSG_COLLECTION_NOT_FOUND,
                        MAP_CODE.get(MSG_COLLECTION_NOT_FOUND)));
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
            throw new ResourceNotFoundException(MSG_USER_NOT_FOUND,
                    MAP_CODE.get(MSG_USER_NOT_FOUND));
        }

        var col = collectionRepo.findColById(collectionId)
                .orElseThrow(() -> new AccessDeniedException(MSG_NO_PERMISSION_MODIFY_COLLECTION,
                        MAP_CODE.get(MSG_NO_PERMISSION_MODIFY_COLLECTION)));

        if (!Objects.equals(col.getUser().getId(), user.getId())) {
            throw new AccessDeniedException(MSG_NO_PERMISSION_MODIFY_COLLECTION,
                    MAP_CODE.get(MSG_NO_PERMISSION_MODIFY_COLLECTION));
        }

        collectionRepo.delete(col);
    }
}