package com.group7.krisefikser.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * Request DTO for registering an admin.
 * This class contains the email, password, and token of the admin to be registered.
 */
@Data
public class RegisterAdminRequest {
  @Email(message = "Invalid email format")
  @NotBlank(message = "Email is required")
  private String email;
  @NotBlank(message = "Password is required")
  @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
      message = "Password must be min 8 char, contain an uppercase letter, a lowercase letter, "
          + "a number and a special character")
  private String password;
  @NotBlank(message = "Token is required")
  private String token;
}
