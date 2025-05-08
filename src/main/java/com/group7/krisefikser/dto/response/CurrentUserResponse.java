package com.group7.krisefikser.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class represents the response sent to the client when a user requests their current
 * information. It contains the user's email and name.
 */
@Data
@AllArgsConstructor
public class CurrentUserResponse {
  private String email;
  private String name;
}