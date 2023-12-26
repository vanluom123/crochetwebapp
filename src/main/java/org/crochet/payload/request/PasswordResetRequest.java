package org.crochet.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetRequest {
  @NotBlank
  String newPassword;
}
