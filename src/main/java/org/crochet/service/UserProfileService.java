package org.crochet.service;

import org.crochet.payload.request.UserProfileRequest;
import org.crochet.payload.response.UserProfileResponse;

public interface UserProfileService {
    UserProfileResponse loadUserProfile();

    UserProfileResponse updateUserProfile(UserProfileRequest request);
}
