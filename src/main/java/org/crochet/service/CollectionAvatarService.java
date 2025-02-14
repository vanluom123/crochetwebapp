package org.crochet.service;

import org.crochet.model.Collection;
import org.crochet.model.FreePattern;

public interface CollectionAvatarService {
    void updateAvatar(Collection collection, FreePattern freePattern);
    void updateAvatarFromNextPattern(Collection collection);
}
