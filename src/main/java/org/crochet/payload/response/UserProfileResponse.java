package org.crochet.payload.response;

import java.time.LocalDate;
import java.util.List;

import org.crochet.enumerator.AuthProvider;
import org.crochet.enumerator.RoleType;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResponse {
    private String id;
    private String name;
    private String email;
    private String imageUrl;
    private AuthProvider provider;
    private boolean emailVerified;
    private RoleType role;
    
    // Profile info
    private String phone;
    private LocalDate birthDate;
    private String gender;
    private String backgroundImageUrl;
    
    // Collections
    private List<CollectionResponse> collections;
    
    // Recent activities
    private List<CommentResponse> recentComments;
}
