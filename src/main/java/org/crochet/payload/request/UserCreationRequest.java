package org.crochet.payload.request;

import lombok.Data;
import org.crochet.enumerator.RoleType;

@Data
public class UserCreationRequest {
    private String name;
    private String email;
    private String password;
    private RoleType role;
}
