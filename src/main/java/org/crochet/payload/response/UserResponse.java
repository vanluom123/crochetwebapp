package org.crochet.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.crochet.constant.AppConstant;
import org.crochet.enumerator.RoleType;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponse {
    private String id;
    private String name;
    @Email
    private String email;
    private RoleType role;
    @JsonFormat(pattern = AppConstant.DATE_PATTERN)
    private LocalDateTime createdDate;
    @JsonFormat(pattern = AppConstant.DATE_PATTERN)
    private LocalDateTime lastModifiedDate;
}