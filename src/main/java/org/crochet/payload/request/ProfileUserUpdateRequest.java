package org.crochet.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUserUpdateRequest {
    private String name;
    private String imageUrl;
}
