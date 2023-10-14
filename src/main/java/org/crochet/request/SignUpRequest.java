package org.crochet.request;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.crochet.model.RoleType;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignUpRequest {
  @NotBlank
  private String name;

  @NotBlank
  @Email
  private String email;

  @NotBlank
  private String password;

  @Enumerated(EnumType.STRING)
  private RoleType role = RoleType.USER;
}
