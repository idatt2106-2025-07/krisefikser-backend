package com.group7.krisefikser.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for inviting an admin.
 * This class contains the email of the admin to be invited.
 */
@Data
public class InviteAdminRequest {
  @Email(message = "Invalid email format")
  @NotBlank(message = "Email is required")
  private String email;
}
