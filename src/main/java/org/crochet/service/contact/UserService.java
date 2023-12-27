package org.crochet.service.contact;

import org.crochet.model.User;
import org.crochet.payload.request.SignUpRequest;

public interface UserService {
    User createUser(SignUpRequest signUpRequest);

    User getByEmail(String email);

    void updatePassword(String password, String email);

    void verifyEmail(String email);
}
