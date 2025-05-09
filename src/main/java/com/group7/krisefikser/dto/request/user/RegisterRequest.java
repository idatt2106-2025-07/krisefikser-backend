package com.group7.krisefikser.dto.request.user;

import com.group7.krisefikser.dto.request.household.HouseholdRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
  @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
      message = "Password must be min 8 char, contain an uppercase letter, a lowercase letter, "
          + "a number and a special character")
  private String password;
  @NotNull(message = "household is required")
  private HouseholdRequest householdRequest;
}
