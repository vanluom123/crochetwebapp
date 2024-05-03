package org.crochet.payload.request;

import lombok.Data;
import org.crochet.enumerator.RoleType;

@Data
public class UserUpdateRequest {
    private String id;
    private String name;
    private RoleType role;
}
