package org.crochet.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.crochet.constant.AppConstant;
import org.crochet.enums.AuthProvider;
import org.crochet.enums.RoleType;

import java.time.LocalDate;
import java.util.List;

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
    private Boolean emailVerified;
    private RoleType role;
    
    // Profile info
    private String phone;
    @JsonFormat(pattern = AppConstant.DATE_NOT_TIME_PATTERN)
    private LocalDate birthDate;
    private String gender;
    private String backgroundImageUrl;
    
    // Collections
    private List<CollectionResponse> collections;
    
    // Recent activities
    private List<CommentResponse> recentComments;
}
