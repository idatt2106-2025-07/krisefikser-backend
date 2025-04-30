package com.group7.krisefikser.dto.response;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class represents the response sent to the client after a successful authentication.
 * It contains the user's email, a message indicating the result of the authentication,
 * the expiration date of the token, and the user's ID.
 */
@Data
@AllArgsConstructor
public class AuthResponse {
  private String email;
  private String message;
  private Date expiryDate;
  private Long id;
}
