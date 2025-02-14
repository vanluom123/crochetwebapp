package org.crochet.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.crochet.constant.AppConstant;

import java.time.LocalDateTime;

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
