package com.group7.krisefikser.dto.request;

import lombok.Data;

/**
 * Request DTO for registering an admin.
 * This class contains the email, password, and token of the admin to be registered.
 */
@Data
public class RegisterAdminRequest {
  private String email;
  private String password;
  private String token;
}
