package com.group7.krisefikser.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class represents the request sent to the server when a user attempts to log in.
 * It contains the user's email and password.
 */
@Data
@AllArgsConstructor
public class LoginRequest {
  @NotNull(message = "email is required")
  private String email;
  @NotNull(message = "password is required")
  private String password;
}
