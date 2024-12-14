package org.crochet.service.impl;

import org.crochet.service.UserProfileService;
import org.crochet.util.SecurityUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import static org.crochet.constant.MessageCodeConstant.MAP_CODE;

import java.util.List;

import org.crochet.constant.MessageConstant;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.User;
import org.crochet.model.UserProfile;
import org.crochet.payload.request.UserProfileRequest;
import org.crochet.payload.response.CommentResponse;
import org.crochet.payload.response.UserProfileResponse;
import org.crochet.repository.CollectionRepo;
import org.crochet.repository.CommentRepository;
import org.crochet.repository.UserProfileRepo;
import org.crochet.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final CollectionRepo colRepo;
    private final CommentRepository commentRepo;
    private final UserProfileRepo userProfileRepo;
    private final UserRepository userRepo;

    @Override
    public UserProfileResponse loadUserProfile() {
        var user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new ResourceNotFoundException(MessageConstant.MSG_USER_NOT_FOUND,
                    MAP_CODE.get(MessageConstant.MSG_USER_NOT_FOUND));
        }

        // Get collections by user id
        var collections = colRepo.getCollectionsByUserId(user.getId());

        // Get recent comments by user id
        List<CommentResponse> recentComments = commentRepo.getRecentCommentsByUserId(user.getId());

        // Get user profile
        var userProfile = user.getUserProfile();
        if (userProfile == null) {
            userProfile = UserProfile.builder()
                    .build();
        }

        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .provider(user.getProvider())
                .emailVerified(user.isEmailVerified())
                .role(user.getRole())
                .phone(userProfile.getPhone())
                .birthDate(userProfile.getBirthDate())
                .gender(userProfile.getGender())
                .backgroundImageUrl(userProfile.getBackgroundImageUrl())
                .collections(collections)
                .recentComments(recentComments)
                .build();
    }

    @Override
    public UserProfileResponse updateUserProfile(UserProfileRequest request) {
        var user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new ResourceNotFoundException(MessageConstant.MSG_USER_NOT_FOUND,
                    MAP_CODE.get(MessageConstant.MSG_USER_NOT_FOUND));
        }

        // Update profile info
        var userProfile = user.getUserProfile();
        if (userProfile == null) {
            userProfile = new UserProfile();
        }
        var isUserProfileUpdated = updateUserProfile(userProfile, request);

        // Update user info
        var isUserInfoUpdated = updateUserInfo(user, request);
        if (isUserInfoUpdated) {
            user = userRepo.save(user);
        }

        // Save changes
        userProfile.setUser(user);
        if (isUserProfileUpdated) {
            userProfile = userProfileRepo.save(userProfile);
        }

        // Create response
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .provider(user.getProvider())
                .emailVerified(user.isEmailVerified())
                .role(user.getRole())
                .phone(userProfile.getPhone())
                .birthDate(userProfile.getBirthDate())
                .gender(userProfile.getGender())
                .backgroundImageUrl(userProfile.getBackgroundImageUrl())
                .build();
    }

    private boolean updateUserProfile(UserProfile userProfile, UserProfileRequest request) {
        boolean isUpdated = false;

        if (request.getPhone() != null) {
            userProfile.setPhone(request.getPhone());
            isUpdated = true;
        }
        if (request.getBirthDate() != null) {
            userProfile.setBirthDate(request.getBirthDate());
            isUpdated = true;
        }
        if (request.getGender() != null) {
            userProfile.setGender(request.getGender());
            isUpdated = true;
        }
        if (request.getBackgroundImageUrl() != null) {
            userProfile.setBackgroundImageUrl(request.getBackgroundImageUrl());
            isUpdated = true;
        }

        return isUpdated;
    }

    private boolean updateUserInfo(User user, UserProfileRequest request) {
        boolean isUpdated = false;
        if (request.getName() != null) {
            user.setName(request.getName());
            isUpdated = true;
        }
        if (request.getImageUrl() != null) {
            user.setImageUrl(request.getImageUrl());
            isUpdated = true;
        }

        return isUpdated;
    }
}
