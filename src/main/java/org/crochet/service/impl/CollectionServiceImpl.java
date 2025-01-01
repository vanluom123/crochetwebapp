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
import org.crochet.payload.response.FreePatternOnHome;
import org.crochet.repository.ColFrepRepo;
import org.crochet.repository.CollectionRepo;
import org.crochet.repository.FreePatternRepository;
import org.crochet.service.CollectionService;
import org.crochet.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
     * @return CollectionResponse
     */
    @Override
    public String addFreePatternToCollection(String collectionId, String freePatternId) {
        var user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new ResourceNotFoundException(MSG_USER_NOT_FOUND,
                    MAP_CODE.get(MSG_USER_NOT_FOUND));
        }

        var collection = collectionRepo.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_COLLECTION_NOT_FOUND,
                        MAP_CODE.get(MSG_COLLECTION_NOT_FOUND)));

        FreePattern freePattern = freePatternRepository.getDetail(freePatternId)
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
                collection.setAvatar(images.get(0).getFileContent());
            } else {
                collection.setAvatar(null);
            }
            collectionRepo.save(collection);
        }

        return "Add success";
    }

    /**
     * Creates a new collection with the specified name for the current user.
     *
     * @param name the name of the collection to be created
     * @return a success message indicating the collection was created successfully
     * @throws BadRequestException       if a collection with the given name already
     *                                   exists
     * @throws ResourceNotFoundException if the user associated with the current
     *                                   session cannot be found
     */
    @Override
    public String createCollection(String name) {
        var user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new ResourceNotFoundException(MSG_USER_NOT_FOUND,
                    MAP_CODE.get(MSG_USER_NOT_FOUND));
        }

        if (collectionRepo.existsCollectionByName(name)) {
            throw new BadRequestException("Collection name already exists");
        }

        Collection collection = new Collection();
        collection.setName(name);
        collection.setUser(user);

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
            throw new ResourceNotFoundException(MSG_USER_NOT_FOUND,
                    MAP_CODE.get(MSG_USER_NOT_FOUND));
        }

        var col = collectionRepo.findCollectionByUserId(collectionId, user.getId())
                .orElseThrow(() -> new AccessDeniedException(MSG_NO_PERMISSION_MODIFY_COLLECTION,
                        MAP_CODE.get(MSG_NO_PERMISSION_MODIFY_COLLECTION)));

        if (col.getName().equals(request.getName())) {
            throw new BadRequestException("Collection name already exists");
        }
        col.setName(request.getName());

        collectionRepo.save(col);

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
            throw new ResourceNotFoundException(MSG_USER_NOT_FOUND,
                    MAP_CODE.get(MSG_USER_NOT_FOUND));
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
            throw new ResourceNotFoundException(MSG_USER_NOT_FOUND,
                    MAP_CODE.get(MSG_USER_NOT_FOUND));
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
            throw new ResourceNotFoundException(MSG_USER_NOT_FOUND,
                    MAP_CODE.get(MSG_USER_NOT_FOUND));
        }

        var col = collectionRepo.findCollectionByUserId(collectionId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(MSG_COLLECTION_NOT_FOUND,
                        MAP_CODE.get(MSG_COLLECTION_NOT_FOUND)));

        var freps = col.getColfreps().stream()
                .map(colFrep -> {
                    var frep = colFrep.getFreePattern();
                    return new FreePatternOnHome(frep.getId(),
                            frep.getName(),
                            frep.getDescription(),
                            frep.getAuthor(),
                            frep.getStatus(),
                            frep.getImages().get(0).getFileContent());
                })
                .toList();

        return new CollectionResponse(col.getId(),
                col.getName(),
                col.getAvatar(),
                col.getColfreps().size(),
                freps);
    }

    /**
     * Deletes a collection associated with the specified collection ID for the currently logged-in user.
     * Ensures the user has the necessary permissions to delete the requested collection.
     *
     * @param collectionId the unique identifier of the collection to be deleted
     * @throws ResourceNotFoundException if the current user cannot be retrieved
     * @throws AccessDeniedException if the user does not have permission to modify the specified collection
     */
    @Override
    public void deleteCollection(String collectionId) {
        var user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new ResourceNotFoundException(MSG_USER_NOT_FOUND,
                    MAP_CODE.get(MSG_USER_NOT_FOUND));
        }

        var col = collectionRepo.findCollectionByUserId(collectionId, user.getId())
                .orElseThrow(() -> new AccessDeniedException(MSG_NO_PERMISSION_MODIFY_COLLECTION,
                        MAP_CODE.get(MSG_NO_PERMISSION_MODIFY_COLLECTION)));

        collectionRepo.delete(col);
    }


    /**
     * Get all free patterns in a collection
     *
     * @param collectionId collection id
     * @return list of free patterns
     */
    @Override
    public List<FreePatternOnHome> getFreePatternsInCollection(String collectionId) {
        var user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new ResourceNotFoundException(MSG_USER_NOT_FOUND,
                    MAP_CODE.get(MSG_USER_NOT_FOUND));
        }
        return freePatternRepository.getFrepsByCollection(user.getId(), collectionId);
    }
}