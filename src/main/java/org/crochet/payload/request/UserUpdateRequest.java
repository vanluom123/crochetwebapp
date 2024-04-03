package org.crochet.payload.request;

import lombok.Data;
import org.crochet.enumerator.RoleType;

import java.util.UUID;

@Data
public class UserUpdateRequest {
    private UUID id;
    private String name;
    private RoleType role;
}
