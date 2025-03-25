package org.crochet.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.crochet.model.Collection;
import org.crochet.model.FreePattern;
import org.crochet.repository.ColFrepRepo;
import org.crochet.repository.CollectionRepo;
import org.crochet.service.CollectionAvatarService;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CollectionAvatarServiceImpl implements CollectionAvatarService {

    private final CollectionRepo collectionRepo;
    private final ColFrepRepo colFrepRepo;

    @Override
    public void updateAvatar(Collection collection, FreePattern freePattern) {
        if (!freePattern.getImages().isEmpty()) {
            var firstImage = freePattern.getImages()
                    .stream()
                    .findFirst()
                    .get()
                    .getFileContent();
            collection.setAvatar(firstImage);
            collectionRepo.save(collection);
        }
    }

    @Override
    public void updateAvatarFromNextPattern(Collection collection) {
        var remainingPatterns = colFrepRepo.findFirstFreePatternByCollectionId(collection.getId());
        if (!remainingPatterns.isEmpty()) {
            updateAvatar(collection, remainingPatterns.getFirst());
        } else {
            // Clear the avatar when no patterns remain in collection
            collection.setAvatar(null);
            collectionRepo.save(collection);
        }
    }
}
