package org.crochet.service;

import org.crochet.model.User;
import org.crochet.payload.request.SignUpRequest;
import org.crochet.payload.request.UserUpdateRequest;
import org.crochet.payload.response.PaginationResponse;
import org.crochet.payload.response.UserResponse;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {
    User createUser(SignUpRequest signUpRequest);

    PaginationResponse<UserResponse> getAll(int offset, int limit, String sortBy, String sortDir, Specification<User> spec);

    @Transactional
    void updateUser(UserUpdateRequest request);

    @Transactional
    void deleteUser(String id);

    void deleteMultipleUsers(List<String> ids);

    User getByEmail(String email);

    User getById(String id);

    UserResponse getDetail(String id);

    void updatePassword(String password, String email);

    void verifyEmail(String email);

    User validateUserCredentials(String email, String password);
}
