package org.crochet.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthResponse {
    private String email;
    private String role;
    private String accessToken;
    @Builder.Default
    private String tokenType = "Bearer";
}
