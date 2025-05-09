package com.group7.krisefikser.dto.request.article;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request class for updating the registered privacy policy.
 * This class contains a single field for the registered privacy policy.
 */
@Data
public class UpdateUnregisteredPrivacyPolicyRequest {
  @NotBlank(message = "Unregistered privacy policy cannot be blank")
  private String unregistered;
}
