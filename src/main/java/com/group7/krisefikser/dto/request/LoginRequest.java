package com.group7.krisefikser.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class represents the request sent to the server when a user attempts to log in.
 * It contains the user's email and password.
 */
@Data
@AllArgsConstructor
public class LoginRequest {
  private String email;
  private String password;
}
