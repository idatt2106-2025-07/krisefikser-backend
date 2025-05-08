package com.group7.krisefikser.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for the request to reset a password link.
 * It contains the email address of the user requesting the password reset.
 * This class is used to validate the input data for the password reset request.
 * It ensures that the email is not blank and is in a valid email format.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordLinkRequest {

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  private String email;
}
