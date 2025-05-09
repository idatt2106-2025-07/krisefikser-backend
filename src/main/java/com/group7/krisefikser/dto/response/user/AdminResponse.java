package com.group7.krisefikser.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class represents the response sent to super admins when they fetch a list of admins.
 * It contains the admin's ID and email.
 * This class is used to provide a simplified view of the admin's information
 * without exposing sensitive data such as passwords.
 */
@Data
@AllArgsConstructor
public class AdminResponse {
  private Long id;
  private String email;
}
