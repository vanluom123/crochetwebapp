package org.crochet.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
  private Long id;
  private String name;
  private String email;
  private String imageUrl;
  private Boolean emailVerified;
  private String providerId;
  private String verificationCode;
}
