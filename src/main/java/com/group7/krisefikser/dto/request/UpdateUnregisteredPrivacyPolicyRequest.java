package com.group7.krisefikser.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUnregisteredPrivacyPolicyRequest {
  @NotBlank(message = "Unregistered privacy policy cannot be blank")
  private String unregistered;
}
