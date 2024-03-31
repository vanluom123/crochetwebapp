package org.crochet.service;

import org.crochet.model.User;
import org.crochet.payload.request.SignUpRequest;
import org.crochet.payload.request.UserUpdateRequest;
import org.crochet.payload.response.UserPaginationResponse;
import org.crochet.payload.response.UserResponse;
import org.crochet.repository.Filter;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(SignUpRequest signUpRequest);

    UserPaginationResponse getAll(int pageNo,
                                  int pageSize,
                                  String sortBy,
                                  String sortDir, List<Filter> filters);

    @Transactional
    void updateUser(UserUpdateRequest request);

    @Transactional
    void deleteUser(UUID id);

    User getByEmail(String email);

    User getById(UUID id);

    UserResponse getDetail(UUID id);

    void updatePassword(String password, String email);

    void verifyEmail(String email);

    User checkLogin(String email, String password);
}
