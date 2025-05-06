package com.group7.krisefikser.dto.request;

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
  private String token;
  private String newPassword;
}
