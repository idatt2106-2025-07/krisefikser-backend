package com.group7.krisefikser.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class represents the response sent to the client after a successful authentication.
 * It contains the user's email.
 */
@Data
@AllArgsConstructor
public class SuperAdminResponse {
  private String email;
}
