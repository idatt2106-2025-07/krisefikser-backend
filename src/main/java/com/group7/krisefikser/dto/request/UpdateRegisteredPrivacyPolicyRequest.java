package com.group7.krisefikser.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateRegisteredPrivacyPolicyRequest {
  @NotBlank(message = "Registered privacy policy cannot be blank")
  private String registered;
}
