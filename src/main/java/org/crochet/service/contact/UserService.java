package org.crochet.service.contact;

import org.crochet.model.User;
import org.crochet.payload.request.SignUpRequest;

import java.util.UUID;

public interface UserService {
    User createUser(SignUpRequest signUpRequest);

    User getByEmail(String email);

    User getById(UUID id);

    void updatePassword(String password, String email);

    void verifyEmail(String email);

    User checkLogin(String email, String password);
}
