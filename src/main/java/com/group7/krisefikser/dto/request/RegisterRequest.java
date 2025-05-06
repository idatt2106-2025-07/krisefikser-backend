package com.group7.krisefikser.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class represents the request sent to the server when a user attempts to register.
 * It contains the user's name, email, and password.
 */
@Data
@AllArgsConstructor
public class RegisterRequest {
  @NotNull(message = "name is required")
  private String name;
  @Email(message = "Invalid email format")
  @NotNull(message = "email is required")
  private String email;
  @NotNull(message = "password is required")
  private String password;
}
