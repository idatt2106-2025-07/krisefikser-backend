package com.group7.krisefikser.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for two-factor login.
 * This class contains the token required for two-factor authentication.
 */
@Data
public class TwoFactorLoginRequest {
  @NotBlank(message = "Token is required")
  private String token;
}
