package com.group7.krisefikser.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request class for updating the registered privacy policy.
 * This class contains a single field for the registered privacy policy.
 */
@Data
public class UpdateRegisteredPrivacyPolicyRequest {
  @NotBlank(message = "Registered privacy policy cannot be blank")
  private String registered;
}
