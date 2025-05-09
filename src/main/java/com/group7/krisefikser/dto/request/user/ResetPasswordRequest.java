package com.group7.krisefikser.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for resetting a user's password.
 * This class contains the token, email, old password, and new password of the user.
 * It is used to send a request to the server to reset the user's password.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
  @NotBlank(message = "token is required")
  private String token;
  @Email(message = "Invalid email format")
  @NotNull(message = "Email is required")
  private String email;
  @NotBlank(message = "password is required")
  private String newPassword;
}
