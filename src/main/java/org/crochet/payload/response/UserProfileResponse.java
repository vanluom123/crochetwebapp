package org.crochet.payload.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.crochet.constant.AppConstant;
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
    @JsonFormat(pattern = AppConstant.DATE_PATTERN)
    private LocalDateTime birthDate;
    private String gender;
    private String backgroundImageUrl;
    
    // Collections
    private List<CollectionResponse> collections;
    
    // Recent activities
    private List<CommentResponse> recentComments;
}
