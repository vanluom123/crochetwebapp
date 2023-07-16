package org.crochet.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.crochet.model.AuthProvider;

@Setter
@Getter
@Accessors(chain = true)
public class UserRequest {
  private Long id;
  private String name;
  @Email
  private String email;
  private String imageUrl;
  private Boolean emailVerified;
  private String password;
  @NotNull
  private AuthProvider provider;
  private String providerId;
  private String verificationCode;
}