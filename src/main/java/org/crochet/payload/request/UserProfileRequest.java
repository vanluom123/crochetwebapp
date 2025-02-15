package org.crochet.payload.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.crochet.constant.AppConstant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRequest {
    private String name;
    private String imageUrl;
    private String phone;
    @JsonFormat(pattern = AppConstant.DATE_PATTERN)
    private LocalDateTime birthDate;
    private String gender;
    private String backgroundImageUrl;
}
