package org.crochet.service;

import org.crochet.model.User;
import org.crochet.payload.request.SignUpRequest;
import org.crochet.payload.request.UserUpdateRequest;
import org.crochet.payload.response.UserPaginationResponse;
import org.crochet.payload.response.UserResponse;
import org.crochet.repository.Filter;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {
    User createUser(SignUpRequest signUpRequest);

    UserPaginationResponse getAll(int pageNo, int pageSize, String sortBy, String sortDir,
                                  String searchText, List<Filter> filters);

    @Transactional
    void updateUser(UserUpdateRequest request);

    @Transactional
    void deleteUser(String id);

    User getByEmail(String email);

    User getById(String id);

    UserResponse getDetail(String id);

    void updatePassword(String password, String email);

    void verifyEmail(String email);

    User checkLogin(String email, String password);
}
