package org.crochet.service;

import org.crochet.model.BaseEntity;

public interface PermissionService {
    void checkUserPermission(BaseEntity entity, String action);
    void validateUserLoggedIn();
    boolean isAdmin();
    boolean isOwner(BaseEntity entity);
}
