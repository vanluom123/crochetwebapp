package org.crochet.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.crochet.constant.AppConstant;
import org.crochet.enums.RoleType;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private String id;
    private String name;
    @Email
    private String email;
    private RoleType role;
    private String imageUrl;
    private Boolean emailVerified;
    @JsonFormat(pattern = AppConstant.DATE_PATTERN)
    private LocalDateTime createdDate;
    @JsonFormat(pattern = AppConstant.DATE_PATTERN)
    private LocalDateTime lastModifiedDate;
}