package com.group7.krisefikser.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class represents the request sent to the server when a user attempts to register.
 * It contains the user's name, email, and password.
 */
@Data
@AllArgsConstructor
public class RegisterRequest {
  private String name;
  private String email;
  private String password;
}
