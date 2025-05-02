package com.group7.krisefikser.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TwoFactorLoginRequest {
  @NotBlank(message = "Token is required")
  private String token;
}
